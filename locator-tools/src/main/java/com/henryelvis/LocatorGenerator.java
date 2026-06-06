package com.henryelvis;

import com.microsoft.playwright.Locator;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import io.github.cdimascio.dotenv.Dotenv;

public class LocatorGenerator 
{
    private final AnthropicChatModel aiModel;

    LocatorGenerator() 
    {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        String apiKey = dotenv.get("ANTHROPIC_API_KEY");

        if (apiKey == null || apiKey.isEmpty())
            throw new IllegalStateException("### Error: ANTHROPIC_API_KEY is missing in the .env file ###");

        this.aiModel = AnthropicChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("claude-3-5-sonnet-latest")
                        .temperature(0.0)
                        .build();
    }

    public String generateLocator(Locator _locator, String _formatType,  String _locatorName)
    {
        String format = _formatType.trim().toLowerCase();

        if (!format.equals("xpath") && !format.equals("locator")) 
            System.out.println("### Invalid format type. Using 'locator' as default. ###");
            format = "locator";

        return askClaude(_locator, format, _locatorName);
    }

    private String askClaude(Locator element, String format, String tagName)
    {
        String outerHTML = (String) element.evaluate("el => el.outerHTML");

        String prompt = """
            Tu es un ingénieur QA senior expert en automatisation de tests.
            Voici le code HTML d'un élément extrait d'une page web :
            %s
            
            Génère le chemin le plus robuste, stable et court possible pour cibler cet élément en utilisant le format : %s.
            
            CONSIGNES STRICTES :
            - Renvoie UNIQUEMENT le chemin brut (ex: //button[@id='submit'] ou page.getByText('Valider')).
            - Ne mets aucune explication, pas de blabla, pas de balises markdown de code (pas de ```).
            - Si l'élément est un %s, adapte la stratégie de localisation en conséquence.
            """.formatted(outerHTML, format, tagName);

        return aiModel.generate(prompt).trim();
    }
}
