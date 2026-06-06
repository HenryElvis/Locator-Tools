package com.henryelvis;

import com.microsoft.playwright.Locator;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import io.github.cdimascio.dotenv.Dotenv;

public class LocatorGenerator 
{
    private final boolean useClaude;

    private AnthropicChatModel claudeAIModel;
    private OllamaChatModel ollamaAIModel;

    public LocatorGenerator(boolean useClaude) 
    {
        this.useClaude = useClaude;

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        if (useClaude && System.getenv("ANTHROPIC_API_KEY") == null) 
            useClaude = false;

        if (useClaude)
        {
            this.claudeAIModel = AnthropicChatModel.builder()
                        .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                        .modelName(System.getenv("CLAUDE_MODEL"))
                        .temperature(getTemp())
                        .build();
        }
        else
        {
            this.ollamaAIModel = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName(System.getenv("OLLAMA_MODEL"))
                        .temperature(getTemp())
                        .build();
        }
    }

    public String generateLocator(Locator _locator, String _formatType,  String _locatorName)
    {
        String format = _formatType.trim().toLowerCase();

        String outerHTML = (String) _locator.evaluate("el => el.outerHTML");

        if (!format.equals("xpath") && !format.equals("locator")) 
        {
            System.out.println("### Invalid format type. Using 'locator' as default. ###");
            format = "locator";
        }

        String finalPrompt = prompt(outerHTML, format, _locatorName);

        if (useClaude)
            return claudeAIModel.generate(finalPrompt).trim();
        else
            return ollamaAIModel.generate(finalPrompt).trim();
    }

    private String prompt(String _outerHTML, String _format, String _tagName)
    {
        return """
            Tu es un ingénieur QA senior expert en automatisation de tests.
            Voici le code HTML d'un élément extrait d'une page web :
            %s
            
            Génère le chemin le plus robuste, stable et court possible pour cibler cet élément en utilisant le format : %s.
            
            CONSIGNES STRICTES :
            - Renvoie UNIQUEMENT le chemin brut (ex: //button[@id='submit'] ou page.getByText('Valider')).
            - Ne mets aucune explication, pas de blabla, pas de balises markdown de code (pas de ```).
            - Si l'élément est un %s, adapte la stratégie de localisation en conséquence.
            """.formatted(_outerHTML, _format, _tagName);
    }

    private double getTemp()
    {
        try 
        {
            return Double.parseDouble(System.getenv("TEMPERATURE"));
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("### Invalid temperature value. Using default 0.0 ###");
            return 0.0;
        }
    }
}
