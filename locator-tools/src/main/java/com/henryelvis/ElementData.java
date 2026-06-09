package com.henryelvis;

public class ElementData
{
    public String id;
    public String className;
    public String textContent;
    public String placeholder;
    public String name;

    public ElementData()
    {

    }

    public ElementData(String _id, String _className, String _textContent, String _placeholder, String _name)
    {
        id = _id;
        className = _className;
        textContent = _textContent;
        placeholder = _placeholder;
        name = _name;
    }

    public ElementData withId(String _id) 
    {
        id = _id;

        return this;
    }

    public ElementData withClassName(String _className)
    {
        className = _className;

        return this;
    }

    public ElementData withTextContent(String _textContent)
    {
        textContent = _textContent;

        return this;
    }

    public ElementData withPlaceholder(String _placeholder)
    {
        placeholder = _placeholder;

        return this;
    }

    public ElementData withName(String _name)
    {
        name = _name;

        return this;
    }
}
