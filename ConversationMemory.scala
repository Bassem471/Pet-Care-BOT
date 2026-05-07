object ConversationMemory{
    def logInteraction(userInput:String,botResponse:String,context:ConversationState):ConversationState=

        val turnNumber=context.history.length+1
        
        val tokens=CoreChatBot.parseInput(userInput)
        val intent=CoreChatBot.detectIntent(tokens)

        val newEntry=InteractionEntry(
            turnNumber=turnNumber,
            userInput=userInput,
            botResponse=botResponse,
            intentDetection=intent
        )

        context.copy(history=context.history:+newEntry)

    def getConversationHistory(context:ConversationState):List[InteractionEntry]=
        context.history

    def getLastNInteractions(n: Int,context:ConversationState):List[InteractionEntry]=
        context.history.takeRight(n)

    def detectRepeatedQuery(input: String, history: List[InteractionEntry]):Boolean=
        val currentTokens=CoreChatBot.parseInput(input).toSet
        history.exists{
            entry=>
                val prevTokens=CoreChatBot.parseInput(entry.userInput).toSet
                val commonTokens=currentTokens.intersect(prevTokens)
                val similarityPercent=0.5
                val similarity=commonTokens.size.toDouble/currentTokens.size.toDouble
                similarity>=similarityPercent

        }
}   