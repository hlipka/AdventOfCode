package de.hendriklipka.aoc.math;

import de.hendriklipka.aoc.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Solves systems of linear integer equations
 */
public class LinearIntegerEquationSolver
{
    private final List<Equation> _equations=new ArrayList<>();

    private long[] _values={};

    boolean isValid=true;

    private final List<String> _varNames=new ArrayList<>();

    public void addEquation(Equation equation)
    {
        _equations.add(equation);
    }

    // solves the system directly, assuming it has exactly one solution
    public void solveExactly()
    {
        // collect all variables and put them into a fixed order
        collectVariables();

        // create a matrix of the correct dimension
        final var matrix = createMatrixFromEquations();

        // gaussian elimination
        reduceMatrix(matrix);

        // back substitution - loop over the rows bottom to top
        isValid=substituteMatrix(matrix, _values);
    }

    private boolean substituteMatrix(final long[][] matrix, final long[] values)
    {
        // we have more equations than variables (overspecified) - needs to solve by other method
        if (_equations.size()>_values.length)
        {
            return false;
        }
        for (int row = _equations.size() - 1; row >= 0; row--)
        {
            // the variable we would resolve here is not set - needs to solve by other method
            if (matrix[row][row]==0)
                return false;
            // subtract known values from the result
            for (int i = values.length - 1; i > row; i--)
            {
                final var columnVarValue = values[i];
                // the variable we would need to solve is not known yet - needs to solve by other method
                if (columnVarValue==Long.MAX_VALUE)
                {
                    return false;
                }
                matrix[row][matrix[row].length - 1] -= columnVarValue * matrix[row][i];
            }
            // and then we know the variable for the current row
            // verify that this is an integer
            if (matrix[row][matrix[row].length - 1] % matrix[row][row] != 0)
            {
                return false;
            }
            values[row]= matrix[row][matrix[row].length - 1] / matrix[row][row];
        }
        return true;
    }

    public SolveContext solveForBestResult(SolveContext context)
    {
        // collect all variables and put them into a fixed order
        collectVariables();

        // create a matrix of the correct dimension
        // row, column
        final var matrix = createMatrixFromEquations();

        // gaussian elimination / re-ordering
        reduceMatrix(matrix);

        // test if we can solve this directly for an exact solution
        long[] values=Arrays.copyOf(_values,_values.length);
        if (substituteMatrix(copyMatrix(matrix), values))
        {
            return context.copy(values);
        }

        // if not, solve by searching through all potential values of the free variables
        return solveWithSearch(matrix, _values, context);
    }

    /*
      recursively solve the system
      first, try to resolve any variables for which we can determine a value (might need multiple runs)
      from the bottom, then find the first row where not all variables are known:
        determine the range for the right-most variable
        loop over the values
        recursively solve the remaining system:
          set the determined value to all places where its used
          search for rows with now-solved values, set these values as well
          start again from the bottom
      also, select the best result from the solutions we find
     */
    private SolveContext solveWithSearch(final long[][] matrix, final long[] values, final SolveContext context)
    {
        // when we get into an invalid state here we stop searching
        if (!determineKnownVariables(matrix, values, context))
        {
            return null;
        }
        boolean foundFreeVariable=false;
        SolveContext bestContext=null;
        for (int row=matrix.length-1; row>=0; row--)
        {
            int freeVariable= getFreeVariable(matrix, row);
            if (-1!=freeVariable)
            {
                foundFreeVariable=true;
                Pair<Long, Long> range=context.getVariableRange(getVarName(freeVariable));
                for (long value=range.getLeft(); value<=range.getRight(); value++)
                {
                    final var newMatrix = copyMatrix(matrix);
                    values[freeVariable]=value;
                    // replace the current variable in all rows
                    replaceVariable(newMatrix, freeVariable, value);
                    SolveContext result = solveWithSearch(newMatrix, values, context);
                    // nothing found
                    if (null==result)
                        continue;
                    if (bestContext==null)
                    {
                        bestContext=result;
                    }
                    else if (result.isBetterThan(bestContext.getValue()))
                    {
                        bestContext=result;
                    }
                }
                // clean up the variable we just looked at
                values[freeVariable] = Long.MAX_VALUE;
                // we can stop here - the recursion tested any other possible free variable
                break;
            }
        }
        // when there are no free variables anymore, we obviously must have a solved system
        // (or we returned earlier with an invalid state)
        if (!foundFreeVariable)
        {
            return context.copy(values);
        }
        else
        {
            return bestContext;
        }
    }

    private static void replaceVariable(final long[][] newMatrix, final int freeVariable, final long value)
    {
        for (int r = 0; r < newMatrix.length; r++)
        {
            if (newMatrix[r][freeVariable] != 0)
            {
                newMatrix[r][newMatrix[r].length - 1]-=(newMatrix[r][freeVariable] * value);
                newMatrix[r][freeVariable]=0;
            }
        }
    }

    private static long[][] copyMatrix(final long[][] matrix)
    {
        long[][] newMatrix=new long[matrix.length][];
        for (int i = 0; i < matrix.length; i++)
        {
            newMatrix[i]= Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return newMatrix;
    }

    private int getFreeVariable(final long[][] matrix, final int row)
    {
        int rightMostColumn=-1;
        int varCount=0;
        for (int col=matrix[row].length-2; col>=0; col--)
        {
            if (matrix[row][col] != 0)
            {
                varCount++;
                if (rightMostColumn == -1)
                {
                    rightMostColumn=col;
                }
            }
        }
        return varCount>1?rightMostColumn:-1;
    }

    /*
        look through all rows, and replace known variables (row where only one column is non-zero)
     */
    private boolean determineKnownVariables(final long[][] matrix, final long[] values, SolveContext context)
    {
        while (true)
        {
            boolean didReplace = false;
            for (int row = 0; row < matrix.length; row++)
            {
                int varToReplace = findVarToReplace(matrix, row);
                if (-1 != varToReplace)
                {
                    // variable would not be an integer - this means the current matrix is not valid
                    if (matrix[row][matrix[row].length - 1] % matrix[row][varToReplace] != 0)
                    {
                        return false;
                    }
                    final var varValue = matrix[row][matrix[row].length - 1] / matrix[row][varToReplace];
                    // check whether the variable is valid in out current context
                    if (!context.variableIsValid(varValue))
                        return false;
                    // clean out the row
                    matrix[row][varToReplace] = 0;
                    matrix[row][matrix[row].length - 1] = 0;
                    // store the value
                    values[varToReplace] = varValue;
                    // replace the other occurrences of this variable
                    for (int i = 0; i < matrix.length; i++)
                    {
                        if (matrix[i][varToReplace] != 0)
                        {
                            matrix[i][matrix[i].length - 1]-= matrix[i][varToReplace] * varValue;
                            matrix[i][varToReplace]=0;
                        }
                    }
                    didReplace = true;
                    // start again from the top
                    break;
                }
            }
            if (!didReplace)
                break;
        }
        return true;
    }

    private int findVarToReplace(final long[][] matrix, final int row)
    {
        int pos=-1;
        final var colCount = matrix[row].length - 1;
        for (int col = 0; col < colCount; col++)
        {
            if (matrix[row][col] != 0)
            {
                // first column found
                if (pos==-1)
                    pos=col;
                else
                    // second column found
                    return -1;
            }
        }
        return pos;
    }

    /*
        Reduce the matrix to diagonal form, using gaussian elimination
        Additionally, before subtracting rows, we scale both rows to their LCM so everything stays an integer value
     */
    private void reduceMatrix(final long[][] matrix)
    {
        int h=0;
        int k=0;
        final var n = matrix[0].length;
        final var m = matrix.length;
        while (h < m && k < n)
        {
            // find the pivot
            int iMax= getColumnMaxPos(matrix, k, h, m);
            if (matrix[iMax][k] == 0)
            {
                k++;
            }
            else
            {
                swapRows(matrix, h, iMax);
                // Do for all rows below pivot
                for (int i=h+1; i<m;i++)
                {
                    //scale both rows to their LCM beforehand so f is always an integer
                    final var a = matrix[h][k];
                    final var b = matrix[i][k];
                    long lcm=MathUtils.lcm(a, b);
                    if (lcm!=0)
                        scaleRow(matrix, h, Math.abs(lcm / a));
                    if (lcm != 0)
                        scaleRow(matrix, i, Math.abs(lcm / b));
                    long f= matrix[i][k] / matrix[h][k];
                    // Fill with zeros the lower part of pivot column
                    matrix[i][k]=0;
                    // Do for all remaining elements in current row
                    for (int j=k+1; j<n;j++)
                    {
                        matrix[i][j]= matrix[i][j] - matrix[h][j] * f;
                    }
                }
                h++;
                k++;
            }
        }
    }

    private void scaleRow(final long[][] matrix, final int row, final long factor)
    {
        for (int col=0;col<matrix[row].length;col++)
        {
            matrix[row][col]=factor*matrix[row][col];
        }
    }

    private long[][] createMatrixFromEquations()
    {
        long[][] matrix = new long[_equations.size()][_values.length + 1];
        for (int row = 0; row < _equations.size(); row++)
        {
            final Equation equation = _equations.get(row);
            fillInRow(equation, row, matrix);
        }
        return matrix;
    }

    private void swapRows(final long[][] matrix, final int r1, final int r2)
    {
        for (int i=0;i<matrix[0].length;i++)
        {
            long h=matrix[r1][i];
            matrix[r1][i]= matrix[r2][i];
            matrix[r2][i]=h;
        }
    }

    private int getColumnMaxPos(final long[][] matrix, final int column, final int from, final int to)
    {
        int maxPos=-1;
        long maxValue=Long.MIN_VALUE;
        for (int row=from; row<to; row++)
        {
            final var current = Math.abs(matrix[row][column]);
            if (current > maxValue)
            {
                maxPos=row;
                maxValue= current;
            }
        }
        return maxPos;
    }

    private void fillInRow(final Equation equation, final int row, final long[][] matrix)
    {
        for (Map.Entry<String, Long> variable : equation._variables.entrySet())
        {
            int pos= getVarPos(variable.getKey());
            matrix[row][pos]=variable.getValue();
        }
        matrix[row][matrix[row].length-1]=equation._value;
    }

    private int getVarPos(final String variable)
    {
        for (int i = 0; i < _varNames.size(); i++)
        {
            final String e = _varNames.get(i);
            if (e.equals(variable))
            {
                return i;
            }
        }
            _varNames.add(variable);
        return _varNames.size()-1;
    }

    private String getVarName(int varPos)
    {
        return _varNames.get(varPos);
    }

    private void collectVariables()
    {
        int varCount=(int)_equations.stream().flatMap(eq->eq._variables.keySet().stream()).distinct().count();
        _values=new long[varCount];
        Arrays.fill(_values, Long.MAX_VALUE);
    }

    public long getVariable(final String varName)
    {
        return _values[getVarPos(varName)];
    }


    public static class Equation
    {
        long _value;
        Map<String, Long> _variables=new HashMap<>();

        public void addVariable(long factor, String name)
        {
            _variables.put(name, factor);
        }

        public void setValue(long value)
        {
            _value=value;
        }

        public long getValue()
        {
            return _value;
        }

        public Map<String, Long> getVariables()
        {
            return _variables;
        }
    }

    public interface SolveContext
    {
        boolean variableIsValid(long varValue);

        Pair<Long, Long> getVariableRange(String varName);

        SolveContext copy(final long[] values);

        boolean isBetterThan(long otherValue);

        long getValue();
    }
}
