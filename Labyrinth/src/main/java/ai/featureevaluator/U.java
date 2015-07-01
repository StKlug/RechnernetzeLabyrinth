package ai.featureevaluator;

import java.util.HashMap;

public class U extends HashMap<Character, Character>
{
    private static final long serialVersionUID = 1L;

    public Character get(Object index)
    {
        Character element = super.get(index);
        return element == null ? 0 : element;
    }

    public Character set(Character index, Character element)
    {
        element = super.put(index, element);
        return element == null ? 0 : element;
    }

    public int treasures;
    public int distance;
    public char score;
}
