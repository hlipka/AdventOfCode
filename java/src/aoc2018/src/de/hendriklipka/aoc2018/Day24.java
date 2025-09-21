package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day24 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day24().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> setup = data.getStringBlocks();
        Army immune=new Army(setup.getFirst());
        Army virus=new Army(setup.get(1));
        immune.setEnemy(virus);
        virus.setEnemy(immune);

        return Math.abs(simulateFight(immune, virus));
    }

    private static int simulateFight(final Army immune, final Army virus)
    {
        String lastV="";
        String lastI="";
        while (!immune._groups.isEmpty() && !virus._groups.isEmpty())
        {
            // select targets
            List<Group> allGroups = ListUtils.union(immune._groups, virus._groups);
            // clean all 'attacked by' relations
            allGroups.forEach((group)-> group.setAttacker(null));
            allGroups.sort(Comparator.comparingInt(Group::getEffectivePower).thenComparingInt(Group::getInitiative).reversed());
            // select targets
            allGroups.forEach(Group::selectTarget);

            // attack
            allGroups.sort(Comparator.comparingInt(Group::getInitiative).reversed());
            allGroups.forEach(Group::attackTarget);

            // we might run into a loop: when there are only a few targets left, each with many hit points, the first group attacking it might not have
            // enough power to even kill one unit - in that case nothing will change at all
            if (lastV.equals(virus.toString()) && lastI.equals(immune.toString()))
            {
                // in that case, return '0' for "stalemate"
                return 0;
            }
            lastV=virus.toString();
            lastI=immune.toString();

            // remove all groups without units
            immune.cleanup();
            virus.cleanup();
        }
        if (immune._groups.isEmpty())
        {
            return -virus._groups.stream().mapToInt(g -> g._units).sum();
        }
        else
        {
            return immune._groups.stream().mapToInt(g -> g._units).sum();
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> setup = data.getStringBlocks();
        Army immune = new Army(setup.getFirst());
        Army virus = new Army(setup.get(1));
        immune.setEnemy(virus);
        virus.setEnemy(immune);

        // tested - 1570 is enough boost for both the example and the real problem
        for (int boost=1; boost<=1570; boost++)
        {
            System.out.println(boost);
            final var immune1 = immune.getClone();
            final var virus1 = virus.getClone();
            immune1.setEnemy(virus1);
            virus1.setEnemy(immune1);
            immune1.setBoost(boost);
            int result = simulateFight(immune1, virus1);
            if (result > 0)
            {
                return result;
            }
        }
        return -1;
    }

    private static class Army
    {
        private final String _name;
        private final List<Group> _groups;

        public Army(List<String> data)
        {
            _name=data.getFirst();
            data.removeFirst();
            _groups = new ArrayList<>(data.stream().map(Group::new).toList());
        }

        private Army(final Army army)
        {
            _name=army._name;
            _groups = new ArrayList<>(army._groups.stream().map(Group::getClone).toList());
        }

        public void setEnemy(Army enemy)
        {
            _groups.forEach(u->u.setEnemy(enemy));
        }

        public String toString()
        {
            return "Army: " + _name + "\n" + StringUtils.join(_groups.stream().map(g-> g._units).toList(), "\n");
        }

        public void cleanup()
        {
            _groups.removeIf(g-> g._units <= 0);
        }

        public Army getClone()
        {
            return new Army(this);
        }

        public void setBoost(final int boost)
        {
            _groups.forEach(g->g.setBoost(boost));
        }
    }

    private static class Group
    {
        int _units;
        int _hitPoints;
        String _damageType;
        int _damage;
        int _initiative;
        List<String> _immuneTo=new ArrayList<>();
        List<String> _weakTo=new ArrayList<>();
        private Army _enemy;
        private Group _attacker;
        private Group _target;

        public Group(String data)
        {
            final List<String> parts = AocParseUtils.parsePartsFromString(data,
                    "(\\d+) units each with (\\d+) hit points (\\(([\\w\\s,;]+)\\))? ?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");
            _units =Integer.parseInt(parts.getFirst());
            _hitPoints =Integer.parseInt(parts.get(1));
            _damageType =parts.get(5);
            _damage =Integer.parseInt(parts.get(4));
            _initiative =Integer.parseInt(parts.get(6));
            parseDamages(_immuneTo, _weakTo, parts.get(3));
        }

        private Group(Group group)
        {
            _units = group._units;
            _hitPoints = group._hitPoints;
            _damageType = group._damageType;
            _damage = group._damage;
            _initiative = group._initiative;
            _immuneTo = new ArrayList<>(group._immuneTo);
            _weakTo = new ArrayList<>(group._weakTo);
            _enemy=group._enemy;
            _attacker=null;
            _target=null;
        }

        private void parseDamages(final List<String> immuneTo, final List<String> weakTo, final String damages)
        {
            if (null==damages)
            {
                return;
            }
            String[] parts=damages.split(";");
            for (String part: parts)
            {
                part=part.trim();
                if (part.startsWith("weak to "))
                    parseDamage(part.substring(8), weakTo);
                if (part.startsWith("immune to "))
                    parseDamage(part.substring(10), immuneTo);
            }
        }

        private void parseDamage(final String s, final List<String> damage)
        {
            String[] parts=s.split(",");
            for (String part: parts)
            {
                damage.add(part.trim());
            }
        }

        public void setEnemy(final Army enemy)
        {
            _enemy=enemy;
        }

        public int getEffectivePower()
        {
            return _damage * _units;
        }

        public int getInitiative()
        {
            return _initiative;
        }

        public void selectTarget()
        {
            Group currentSelectedTarget=null;
            int maxDamage=0;
            for (Group defendingGroup: _enemy._groups)
            {
                if (defendingGroup.getAttacker()!=null)
                {
                    continue;
                }
                int damage=calculateDamage(defendingGroup);
                if (0==damage)
                {
                    continue;
                }
                if (damage>maxDamage)
                {
                    maxDamage=damage;
                    currentSelectedTarget=defendingGroup;
                }
                else if (damage==maxDamage)
                {
                    // higher effective power wins in a tie
                    if (currentSelectedTarget.getEffectivePower()>defendingGroup.getEffectivePower())
                    {
                        continue;
                    }
                    if (currentSelectedTarget.getEffectivePower() == defendingGroup.getEffectivePower())
                    {
                        // higher  initiative wins
                        if (currentSelectedTarget.getInitiative()>defendingGroup.getInitiative())
                        {
                            continue;
                        }
                    }
                    currentSelectedTarget = defendingGroup;
                }
            }
            _target=currentSelectedTarget;
            if (null!=_target)
                _target.setAttacker(this);
        }

        public void setAttacker(final Group group)
        {
            _attacker= group;
        }

        public Group getAttacker()
        {
            return _attacker;
        }

        public void attackTarget()
        {
            // can this group attack, and has it selected a target?
            if (_units>0 && _target!=null)
            {
                _target.dealDamage(calculateDamage(_target));
            }
        }

        private void dealDamage(final int damage)
        {
            int unitsDamaged=damage/_hitPoints;
            _units-=unitsDamaged;
        }

        private int calculateDamage(final Group target)
        {
            if (target._immuneTo.contains(_damageType))
            {
                return 0;
            }
            if (target._weakTo.contains(_damageType))
            {
                return 2*getEffectivePower();
            }
            return getEffectivePower();
        }

        public Group getClone()
        {
            return new Group(this);
        }

        public void setBoost(int boost)
        {
            _damage+=boost;
        }
    }
}
