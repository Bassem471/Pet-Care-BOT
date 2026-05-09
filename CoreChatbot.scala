object CoreChatBot {
  
  def greetUser(): String = {
    "I am your Pet-Care-Bot.\n " +
    "\tI can give you advice on dogs, cats, fish, and birds.\n " +
    "\t\t\t\t\t\t How can I help you Today?"
  }

  def parseInput(input: String): List[String] = {
    input.toLowerCase.replaceAll("[^a-z0-9\\s]", "").split("\\s+").toList
  }

  def detectIntent(tokens: List[String]): Intent = {
    
    if (tokens.contains("favorite") && tokens.contains("pet")) {
      tokens.find(token => List("dog", "cat", "fish", "bird").contains(token)) match {
        case Some(p) => Intent.UpdatePreference("favorite_pet", p)
        case None => Intent.Unknown
      }
    } else if (tokens.contains("prefer") || tokens.contains("like")) {
      tokens.find(token => List("diet", "health", "training", "habitat", "hygiene", "exercise").contains(token)) match {
        case Some(cat) => Intent.UpdatePreference("category", cat)
        case None => Intent.Unknown
      }
    } else if (tokens.contains("recommend") || tokens.contains("suggest")) {
      Intent.GetRecommendation
    } else if (tokens.intersect(List("feed", "diet", "train", "sick", "health", "dog", "fish", "cat", "bird")).nonEmpty) {
      Intent.AskPetQuestion
    } else if (tokens.contains("hi") || tokens.contains("hello")) {
      Intent.Greeting
    }else if (tokens.contains("summary") || tokens.contains("history")){
      Intent.GetSummary 
    }else {
      Intent.Unknown
    }
  }

  def handleUserInput(input: String, state: ConversationState): (String, ConversationState) = {
    val badWords = List("kill", "hurt", "harm", "abuse", "poison", "hit")
    
    if (badWords.exists(word => input.toLowerCase.contains(word))) {
      ("I am a Pet Care BOT, I can't provide these informations\n", state)
    } else {
      val tokens = parseInput(input)
      val userIntent = detectIntent(tokens)
      
      userIntent match {
        case Intent.Greeting => 
          (greetUser(), state)

        case Intent.AskPetQuestion => 
          if (ConversationMemory.detectRepeatedQuery(input, state.history)) {
            ("You asked that before. Ask me for another tip\n", state)
          } else {
            (generateResponse(input), state)
          }

        case Intent.GetRecommendation => 
          (handleRecommendation(state), state)

        case Intent.UpdatePreference(prefType, value) => 
          val newPref = RecommendationEngine.updatePreferences(prefType, value, state.userPreferences)
          val newState = state.copy(userPreferences = newPref)
          val responseMess = if (prefType == "favorite_pet") {
            s"Got it! I'll focus on $value tips for you."
          } else {
            s"Noted! I'll prioritize $value tips."
          }
          (responseMess, newState)
  
        case Intent.GetSummary =>
            val fullHistory = ConversationMemory.getConversationHistory(state)
            val summaryText = ConversationMemory.summarizeConversation(fullHistory)
            val mood = ConversationMemory.getUserMood(fullHistory)
  
            (s"$summaryText\nPet-Care-BOT: Also, you seem to be in a $mood mood today!", state)

        case Intent.Unknown => 
          ("I'm sorry, I didn't quite catch that.\n\tTry asking for recommendations or asking 'what should I feed my pet?'", state)
      }
    }
  }
    
  def handleRecommendation(state: ConversationState): String = {
    val preferences = RecommendationEngine.getUserPreferences(state)
    
    if (preferences.isEmpty) {
      "I'd love to recommend something! But first, tell me:\n" +
      "  - What's your favorite pet? (dog, cat, fish, bird)\n" +
      "  - What would you like help with? (diet, health, training, habitat)"
    } else {
      val recommendations = RecommendationEngine.recommend(preferences, KnowledgeBase.petTips)
      
      if (recommendations.isEmpty) {
        "I couldn't find tips matching your preferences. Try asking about something specific!"
      } else {
        val tip = recommendations.head
        val explanation = RecommendationEngine.explainRecommendation(tip, preferences)
        s"$explanation\n\n${tip.content}"
      }
    }
  }

  def generateResponse(query: String): String = {
    val tokens = parseInput(query)
    val mentionedPet = tokens.find(word => List("dog", "cat", "fish", "bird").contains(word))

    mentionedPet match {
      case Some(pet) =>
        val allTips = KnowledgeBase.petTips.filter(tip => tip.petType.toLowerCase == pet)

        val matchingTip = if (tokens.intersect(List("feed", "diet", "eat")).nonEmpty) {
          allTips.filter(tip => tip.category.toLowerCase == "diet")
        } else if (tokens.intersect(List("train", "training")).nonEmpty) {
          allTips.filter(tip => tip.category.toLowerCase == "training")
        } else if (tokens.intersect(List("sick", "health")).nonEmpty) {
          allTips.filter(tip => tip.category.toLowerCase == "health")
        } else {
          allTips
        }

        if (matchingTip.nonEmpty) {
          val randomIndex = scala.util.Random.nextInt(matchingTip.length)
          val selectedTip = matchingTip(randomIndex)
          s"Here is a ${selectedTip.category} tip for your $pet: ${selectedTip.content}"
        } else {
          s"I don't have any specific tips for a $pet right now."
        }
        
      case None =>
        "That's a great question, but I need to know what kind of pet you have!\nIs it a dog, cat, fish or bird?"
    }
  }
}