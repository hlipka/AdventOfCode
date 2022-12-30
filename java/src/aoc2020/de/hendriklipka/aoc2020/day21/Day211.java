package de.hendriklipka.aoc2020.day21;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 21.12.20
 * Time: 13:51
 */
public class Day211
{
    private static final Set<String> allIngredients=new HashSet<>();
    private static final Map<String, List<List<String>>> mapAllergensToIngredients = new HashMap<>();
    private static final Map<String, String> mapIngredientToAllergen = new HashMap<>();
    private static final List<List<String>> meals = new ArrayList<>();

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("../data/2020/day21.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(Day211::parseFood);
            for (Map.Entry<String,List<List<String>>> entry: mapAllergensToIngredients.entrySet())
            {
                String allergen=entry.getKey();
                for (String ingredient: allIngredients)
                {
                    boolean existsInAll=true;
                    for (List<String> list: entry.getValue())
                    {
                        if (!list.contains(ingredient))
                        {
                            existsInAll=false;
                            break;
                        }
                    }
                    if (existsInAll)
                    {
                        mapIngredientToAllergen.put(ingredient, allergen);
                    }
                }
            }
            System.out.println(allIngredients);
            Set<String> ingredientsWithAllergen = mapIngredientToAllergen.keySet();
            final Collection<String> ingredientsWithoutAllergen = CollectionUtils.disjunction(allIngredients, ingredientsWithAllergen);
            System.out.println(allIngredients.size()+"/"+ ingredientsWithAllergen.size()+"/"+ ingredientsWithoutAllergen.size());
            System.out.println(ingredientsWithAllergen);
            System.out.println();
            System.out.println(ingredientsWithoutAllergen);
            int count=0;
            for (List<String> ingredients: meals)
            {
                for (String ingredient: ingredientsWithoutAllergen)
                {
                    if (ingredients.contains(ingredient))
                    {
                        count++;
                    }
                }
            }
            System.out.println(count);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    static final Pattern FOOD = Pattern.compile("([\\w\\s]+) \\(contains ([\\s\\w,]+)\\)");

    private static void parseFood(final String line)
    {
        Matcher m=FOOD.matcher(line);
        if (!m.matches())
        {
            throw new IllegalArgumentException("cannot parse line:\n"+line);
        }
        String ingredientsStr=m.group(1);
        String allergensStr=m.group(2);
        String[] ingredients=ingredientsStr.split(" ");
        String[] allergens=allergensStr.split(",");
        for (String allergen: allergens)
        {
            addAllergen(allergen.strip(), ingredients);
        }
        meals.add(Arrays.asList(StringUtils.stripAll(ingredients)));
    }

    private static void addAllergen(final String allergen, final String[] ingredients)
    {
        List<List<String>> ingredientsList = mapAllergensToIngredients.computeIfAbsent(allergen, s -> new ArrayList<>());
        ingredientsList.add(Arrays.asList(StringUtils.stripAll(ingredients)));
        Collections.addAll(allIngredients, StringUtils.stripAll(ingredients));
    }
}
