import scala.io.StdIn.readLine
object Main{
    def main (args:Array[String]):Unit=
        println("======STARTING PET CARE CHAT BOT======\n")
        
        println("Pet-Care-BOT: "+CoreChatBot.greetUser())

        chatLoop()
    
    @scala.annotation.tailrec
    def chatLoop():Unit=
        val userInput=readLine("\nYou: ")
        if(userInput.toLowerCase=="exit"||userInput.toLowerCase=="quit"||userInput.toLowerCase=="bye")
            println("Pet-Care-BOT: Goodbye! Enjoy your day!")
            sys.exit(0)
        else
            val botResponse=CoreChatBot.handleUserInput(userInput)
            println(s"Pet-Care-BOT: $botResponse")
        chatLoop()
}