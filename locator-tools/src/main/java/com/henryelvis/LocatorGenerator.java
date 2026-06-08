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
        String claudeModel = dotenv.get("CLAUDE_MODEL", "claude-sonnet-4-6");
        String ollamaModel = dotenv.get("OLLAMA_MODEL", "qwen2.5-coder:7b");

        if (useClaude && (anthropicKey == null || anthropicKey.isBlank())) 
        {
            System.out.println("### ANTHROPIC_API_KEY missing, try to use Ollama locally ###");
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

    /**
     * 
     * @param _locator choose by user
     * @param _formatType can be xpath or playwright locator
     * @param _locatorName balise for element can be button or input etc...
     * @return the result proposed by a model
     */
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
            return e.getMessage();
        }

        String finalPrompt = Prompt(outerHTML, format, _locatorName);

        if (useClaude)
            response = claudeAIModel.chat(finalPrompt);
        else
            response = ollamaAIModel.chat(finalPrompt);

        return response.trim();
    }

    /**
     * 
     * @param _locator to target
     * @param _proposedPath 
     * @param _format can be xpath or playwright locator
     * @param _type of locator can be input or button etc...
     * @return the proposed the new result for locator
     */
    public String AutoCorrectLocator(Locator _locator, String _proposedPath, String _format, String _type)
    {
        String response;
        String element = (String) _locator.evaluate("el => el.outerHTML");
        String finalPrompt = PromptAutoCorrection(element, _proposedPath, _format, _type);

        if (useClaude)
            response = claudeAIModel.chat(finalPrompt);
        else
            response = ollamaAIModel.chat(finalPrompt);

        return response.trim();
    }

    /**
     * 
     * @param _outerHTML html code with balise
     * @param _format can be xpath or playwright locator
     * @param _tagName can be input or button etc...
     * @return the prompt use for model
     */
    private String Prompt(String _outerHTML, String _format, String _tagName)
    {
        return """            
            Tu es un ingénieur QA senior expert en automatisation de tests.
            Voici le code HTML d'un élément extrait d'une page web :
            %s
            
            Génère le chemin le plus robuste, stable et court possible pour cibler cet élément en utilisant le format : %s.
            
            <strategies>
                %s
            </strategies>
            
            CONSIGNES STRICTES :
            1. SI LE FORMAT EST 'locator' :
               - ANALYSE PRIORITAIRE : Si le HTML contient un attribut 'data-test' ou 'data-testid', UTILISE OBLIGATOIREMENT page.getByTestId("valeur").
               - Sinon, privilégie les méthodes d'accessibilité (getByRole, getByPlaceholder, getByLabel).
            
            2. SI LE FORMAT EST 'xpath' :
               - Utilise uniquement des expressions XPath standard et robustes.
               - Priorise les attributs uniques comme @id ou @data-test (ex: //input[@data-test='login-button']).
            
            3. RÈGLES COMMUNES :
               - Pour les listes ou conteneurs répétitifs, N'UTILISE JAMAIS d'index (.first(), .nth()). Utilise OBLIGATOIREMENT la méthode .filter() (si format 'locator') ou une condition XPath sur un parent/attribut unique.
               - Renvoie UNIQUEMENT la ligne de code.
               - PAS de blabla, PAS de texte introductif, PAS de balises markdown (```).
               - Si l'élément est un %s, adapte la stratégie en conséquence.
            """.formatted(_outerHTML, _format, GetExamples(_format), _tagName);
    }

    /**
     * Prompt for autocorrection
     * @param _outerHTML html code with balise
     * @param _path false locator
     * @param _format can be xpath or playwright locator
     * @param _tagName can be input or button etc...
     * @return a prompt of correction for locator
     */
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

    /**
     * Exemple of prompt for xpath and playwright locator
     * @param _format of locator wanted, can be xpath or playwright locator
     * @return an exemple of locator for xpath and playwrigh locator
     */
    private String GetExamples(String _format)
    {
        if (_format.equals("xpath")) 
        {
            return """
                Exemples attendus pour le format 'xpath' :
                - //input[@id='user-name']
                - //button[@type='submit']
                - //a[contains(@href, 'contact')]
                - //div[@class='login_logo']
                - //span[text()='Valider']
                - //div[@class='product' and .//h2[text()='Backpack']]
                """;
        } 
        else 
        {
            return """
            Exemples API Playwright (Prioriser ces méthodes par ordre de robustesse) :
            
            1. ID TECHNIQUE (PRIORITÉ ABSOLUE) :
            - page.getByTestId("login-button")
            
            2. SÉLECTEURS D'ACCESSIBILITÉ (Si aucun testId présent) :
            - page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"))
            - page.getByPlaceholder("Username")
            - page.getByLabel("Password")
            
            3. FILTRAGE ET HIÉRARCHIE (Pour les éléments répétitifs) :
            - page.locator(".cart_item").filter(new Locator.FilterOptions().setHasText("Backpack")).getByRole(AriaRole.BUTTON)
            """;
        }
    }
}
