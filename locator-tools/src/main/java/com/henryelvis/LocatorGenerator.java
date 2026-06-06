package com.henryelvis;

import java.time.Duration;

import com.microsoft.playwright.Locator;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import io.github.cdimascio.dotenv.Dotenv;

public class LocatorGenerator 
{
    private boolean useClaude;

    private AnthropicChatModel claudeAIModel;
    private OllamaChatModel ollamaAIModel;

    public LocatorGenerator(boolean useClaude) 
    {
        this.useClaude = useClaude;

        Dotenv dotenv = Dotenv.configure()
                            .directory("./locator-tools")
                            .ignoreIfMissing()
                            .load();
        
        if (useClaude && dotenv.get("ANTHROPIC_API_KEY") == null) 
            useClaude = false;

        if (useClaude)
        {
            this.claudeAIModel = AnthropicChatModel.builder()
                        .apiKey(dotenv.get("ANTHROPIC_API_KEY"))
                        .modelName(dotenv.get("CLAUDE_MODEL"))
                        .temperature(GetTemp(dotenv))
                        .build();
        }
        else
        {
            this.ollamaAIModel = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("qwen2.5-coder:7b")
                        .temperature(0.0)
                        .timeout(Duration.ofMinutes(5))
                        .build();
        }
    }

    public String GenerateLocator(Locator _locator, String _formatType,  String _locatorName)
    {
        String response;
        String outerHTML;
        String format = (_formatType == null) ? "locator" : _formatType.trim().toLowerCase();

        if (!format.equals("xpath") && !format.equals("locator")) 
        {
            System.out.println("### Invalid format type. Using 'locator' as default. ###");
            format = "locator";
        }

        try
        {
            outerHTML = (String) _locator.evaluate("el => el.outerHTML");
        }
        catch (Exception e)
        {
            return "### [ERREUR PLAYWRIGHT] Impossible de récupérer le HTML de l'élément : " + e.getMessage() + " ###";
        }

        String finalPrompt = Prompt(outerHTML, format, _locatorName);

        if (useClaude)
            response = claudeAIModel.generate(finalPrompt);
        else
            response = ollamaAIModel.generate(finalPrompt);

        if (response == null || response.isBlank()) 
            return "### [Ollama a renvoyé du vide] Le modèle est sans doute en cours de chargement dans ta RAM, réessaie dans 30 secondes. ###";

        return response.trim();
    }

    private String Prompt(String _outerHTML, String _format, String _tagName)
    {
        String exemples;
        
        if (_format.equals("xpath"))
        {
            exemples = """
                Exemples attendus pour le format 'xpath' :
                - //input[@id='user-name']
                - //button[@type='submit']
                - //div[@class='login_logo']
                """;
        }
        else 
        {
            exemples = """
                Exemples attendus pour le format 'locator' (API Playwright Java) :
                - page.locator("#user-name")
                - page.getByPlaceholder("Username")
                - page.locator("[data-test='username']")
                - page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"))
                """;
        }

        return """
            Tu es un ingénieur QA senior expert en automatisation de tests.
            Voici le code HTML d'un élément extrait d'une page web :
            %s
            
            Génère le chemin le plus robuste, stable et court possible pour cibler cet élément en utilisant le format : %s.
            
            CONSIGNES STRICTES :
            - Renvoie UNIQUEMENT le chemin brut (ex: //button[@id='submit'] ou page.getByText('Valider')).
            - Ne mets aucune explication, pas de blabla, pas de balises markdown de code (pas de ```).
            - Si l'élément est un %s, adapte la stratégie de localisation en conséquence.
            """.formatted(_outerHTML, _format, exemples, _tagName);
    }

    private double GetTemp(Dotenv _dotenv)
    {
        try 
        {
            return Double.parseDouble(_dotenv.get("TEMPERATURE"));
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("### Invalid temperature value. Using default 0.0 ###");

            return 0.0;
        }
    }
}
