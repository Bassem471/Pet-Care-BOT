object ConversationMemory {

  def logInteraction(userInput: String, botResponse: String, context: ConversationState): ConversationState = {
    val turnNumber = context.history.length + 1
    
    val tokens = CoreChatBot.parseInput(userInput)
    val intent = CoreChatBot.detectIntent(tokens)

    val newEntry = InteractionEntry(
      turnNumber = turnNumber,
      userInput = userInput,
      botResponse = botResponse,
      intentDetection = intent
    )

    context.copy(history = context.history :+ newEntry)
  }

  def getConversationHistory(context: ConversationState): List[InteractionEntry] = {
    context.history
  }

  def getLastNInteractions(n: Int, context: ConversationState): List[InteractionEntry] = {
    context.history.takeRight(n)
  }

  def detectRepeatedQuery(input: String, history: List[InteractionEntry]): Boolean = {
    val currentTokens = CoreChatBot.parseInput(input).toSet
    if (currentTokens.isEmpty) {
      false
    } else {
      history.exists { entry =>
        val prevTokens = CoreChatBot.parseInput(entry.userInput).toSet
        val commonTokens = currentTokens.intersect(prevTokens)
        val similarityPercent = 0.5
        val similarity = commonTokens.size.toDouble / currentTokens.size.toDouble
        similarity >= similarityPercent
      }
    }
  }

  
  def extractTopics(history: List[InteractionEntry]): List[String] = {
    history.map { entry =>
      entry.intentDetection match {
        case Intent.Greeting => "Greeting"
        case Intent.AskPetQuestion => "Pet_question"
        case Intent.GetRecommendation => "Recommendation"
        case Intent.UpdatePreference(prefType, _) => prefType
        case Intent.GetSummary=>"Summary"
        case Intent.Unknown => "Unknown"
      }
    }.distinct
  }

  def summarizeConversation(history: List[InteractionEntry]): String = {
    if (history.isEmpty) {
      "No conversation history available."
    } else {
      val totalInteractions = history.length
      val petsMentioned = history.flatMap { entry =>
        val tokens = CoreChatBot.parseInput(entry.userInput)
        tokens.filter(word => List("dog", "cat", "fish", "bird").contains(word))
      }
      
      val petCount = petsMentioned.groupBy(identity).view.mapValues(_.size).toMap
      val petSummary = if (petCount.isEmpty) {
        "No specific pets mentioned."
      } else {
        petCount.map { case (pet, count) => if (count == 1) s"$pet" else s"$pet ($count times)" }.mkString(", ")
      }

      s"We've discussed $totalInteractions topics. You asked about: $petSummary."
    }
  }

  def getMostDiscussedTopics(history: List[InteractionEntry]): List[(String, Int)] = {
    if (history.isEmpty) {
      Nil
    } else {
      val allTopics = history.flatMap { entry =>
        val tokens = CoreChatBot.parseInput(entry.userInput)
        tokens.filter(word => List("dog", "cat", "fish", "bird", "diet", "health", "training", "habitat", "hygiene").contains(word))
      }
      val groupedTopics = allTopics.groupBy(identity)
      val topicCounts = groupedTopics.view.mapValues(_.size)
      
      val topicList = topicCounts.toList
      topicList.sortBy(_._2).reverse
    }
  }

  def getUserMood(history: List[InteractionEntry]): String = {
    if (history.isEmpty) {
      "Neutral"
    } else {
      val goodWords = List("great", "love", "good", "awesome", "thanks", "perfect", "excellent", "helpful", "amazing")
      val badWords = List("boring", "bad", "hate", "terrible", "awful", "useless", "annoying", "frustrated", "disappointed")
      
      val (positiveCount, negativeCount) = history.foldLeft((0, 0)) { case ((posCount, negCount), entry) =>
        val tokens = CoreChatBot.parseInput(entry.userInput)
        val posWords = tokens.count(token => goodWords.contains(token))
        val negWords = tokens.count(token => badWords.contains(token))
        (posCount + posWords, negCount + negWords)
      }
      
      if (positiveCount > negativeCount && positiveCount > 0) "Positive"
      else if (negativeCount > positiveCount && negativeCount > 0) "Frustrated"
      else "Neutral"
    }
  }
}