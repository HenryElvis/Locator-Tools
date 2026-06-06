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
                            .ignoreIfMissing()
                            .load();

        if (dotenv.get("OLLAMA_MODEL") == null && dotenv.get("ANTHROPIC_API_KEY") == null) 
        {
            dotenv = Dotenv.configure()
                        .directory("../")
                        .ignoreIfMissing()
                        .load();
        }
        
        String anthropicKey = dotenv.get("ANTHROPIC_API_KEY");
        String claudeModel = dotenv.get("CLAUDE_MODEL", "claude-3-5-sonnet-latest");
        String ollamaModel = dotenv.get("OLLAMA_MODEL", "qwen2.5-coder:7b");

        if (useClaude && (anthropicKey == null || anthropicKey.isBlank())) 
        {
            System.out.println("### ANTHROPIC_API_KEY missing, using Ollama locally ###");
            this.useClaude = false;
        }

        if (useClaude)
        {
            this.claudeAIModel = AnthropicChatModel.builder()
                        .apiKey(anthropicKey)
                        .modelName(claudeModel)
                        .temperature(0.0)
                        .build();
        }
        else
        {
            this.ollamaAIModel = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName(ollamaModel)
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

    public String AutoCorrectLocator(Locator _locator, String _proposedPath, String _format, String _type)
    {
        String response;
        String element = (String) _locator.evaluate("el => el.outerHTML");
        String finalPrompt = PromptAutoCorrection(element, _proposedPath, _format, _type);

        if (useClaude)
            response = claudeAIModel.generate(finalPrompt);
        else
            response = ollamaAIModel.generate(finalPrompt);

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
            
            %s
            
            CONSIGNES STRICTES :
            - Renvoie UNIQUEMENT le chemin brut.
            - Ne mets aucune explication, pas de blabla, pas de balises markdown.
            - Si l'élément est un %s, adapte la stratégie de localisation en conséquence.
            """.formatted(_outerHTML, _format, exemples, _tagName);
    }

    private String PromptAutoCorrection(String _outerHTML, String _path, String _format, String _tagName)
    {
        return """
            Le chemin que tu as généré ne cible pas de manière unique l'élément sur la page.
            Voici le code HTML de l'élément à cibler :
            %s
            
            Voici le chemin incorrect que tu as généré :
            %s
            
            Consigne :
            1. Le format attendu est : %s
            2. Le type de balise est : %s
            
            Corrige le chemin pour qu'il soit unique et précis. 
            SI LE PRÉCÉDENT CHEMIN ÉTAIT TROP LARGE, utilise un parent ou un attribut plus discriminant.
            SI LE PRÉCÉDENT CHEMIN NE TROUVAIT RIEN, utilise un sélecteur plus générique.
            Réponds UNIQUEMENT avec le nouveau locator.
            """.formatted(_outerHTML, _path, _format, _tagName);
    }
}
