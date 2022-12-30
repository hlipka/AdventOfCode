package de.hendriklipka.aoc2020.day21;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 21.12.20
 * Time: 13:51
 */
public class Day212
{
    private static final Set<String> allIngredients=new HashSet<>();
    private static final Map<String, List<List<String>>> mapAllergensToIngredients = new HashMap<>();
    private static final Map<String, List<String>> mapIngredientToAllergens = new HashMap<>();

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("data/day21.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(Day212::parseFood);
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
                        // from the list, we might find multiple allergens to be in the food. So we store them all and reduce later
                        List<String> list=mapIngredientToAllergens.computeIfAbsent(ingredient, s -> new ArrayList<>());
                        list.add(allergen);
                    }
                }
            }
            Map<String, String> mapIngredientToAllergen = new HashMap<>();
            final Set<Map.Entry<String, List<String>>> allergensMapping  = mapIngredientToAllergens.entrySet();
            // look for mapping where we have only one allergen - there we know the mapping
            // remember it, remove the allergen from the list (and the mapping)
            // repeat until everything is matched
            while (mapIngredientToAllergens.size() != 0)
            {
                for (Map.Entry<String, List<String>> mapping : allergensMapping)
                {
                    if (mapping.getValue().size() == 1)
                    {
                        final String allergen = mapping.getValue().get(0);
                        removeAllergen(allergen);
                        mapIngredientToAllergen.put(mapping.getKey(), allergen);
                        mapIngredientToAllergens.remove(mapping.getKey());
                        break;
                    }
                }
            }
            List<String> sorted = mapIngredientToAllergen.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList());
            System.out.println(StringUtils.join(sorted, ","));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void removeAllergen(String allergen)
    {
        for (List<String> list: mapIngredientToAllergens.values())
        {
            list.remove(allergen);
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
    }

    private static void addAllergen(final String allergen, final String[] ingredients)
    {
        List<List<String>> ingredientsList = mapAllergensToIngredients.computeIfAbsent(allergen, s -> new ArrayList<>());
        ingredientsList.add(Arrays.asList(StringUtils.stripAll(ingredients)));
        Collections.addAll(allIngredients, StringUtils.stripAll(ingredients));
    }
}
