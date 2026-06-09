package com.henryelvis;

public class ElementData
{
    public String id;
    public String className;
    public String textContent;
    public String placeholder;
    public String name;

    public String typeOfLocator;
    public String formatOfLocator;

    public ElementData()
    {}

    public ElementData(String _id, String _className, String _textContent, String _placeholder, String _name, String _typeOfLocator, String _formatOfLocator)
    {
        id = _id;
        className = _className;
        textContent = _textContent;
        placeholder = _placeholder;
        name = _name;

        typeOfLocator = _typeOfLocator;
        formatOfLocator = _formatOfLocator;
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

    /**
     * Name attribut
     */
    public ElementData withName(String _name)
    {
        name = _name;

        return this;
    }

    /**
     * Type of locator (input, button, a etc...)
     * @param _type 
     * @return
     */
    public ElementData withType(String _type)
    {
        typeOfLocator = _type;

        return this;
    }

    /**
     * Format of locator (xpath, playwright locator)
     * @param _format
     * @return
     */
    public ElementData withFormat(String _format)
    {
        formatOfLocator = _format;

        return this;
    }
}
