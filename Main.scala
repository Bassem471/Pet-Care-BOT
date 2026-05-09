import scala.io.StdIn.readLine

object Main {
  def main(args: Array[String]): Unit = {
    println("======STARTING PET CARE CHAT BOT======\n")
    
    println("Pet-Care-BOT: " + CoreChatBot.greetUser())

    val initialState = ConversationState(
      history = Nil,
      userPreferences = Map.empty,
      user = UserProfile()
    )

    chatLoop(initialState)
  }
  
  @scala.annotation.tailrec
  def chatLoop(state: ConversationState): Unit = {
    val userInput = readLine("\nYou: ")
    
    if (userInput.toLowerCase == "exit" || userInput.toLowerCase == "quit" || userInput.toLowerCase == "bye") {
      println("Pet-Care-BOT: Goodbye! Enjoy your day!")
      sys.exit(0)
    } else {
      val (botResponse, stateAfterResponse) = CoreChatBot.handleUserInput(userInput, state)
      
      println()
      println(s"Pet-Care-BOT: $botResponse")

      val newState = ConversationMemory.logInteraction(
        userInput = userInput,
        botResponse = botResponse,
        context = stateAfterResponse
      )
      
      chatLoop(newState)
    }
  }
}