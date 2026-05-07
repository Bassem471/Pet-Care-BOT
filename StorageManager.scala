import java.io.{FileWriter,PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
object StorageManager{
    val logFile="Chat History.txt"
    def saveInteraction(userInput:String,botResponse:String):Unit=
        val formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val timeStamp=LocalDateTime.now().format(formatter)

        val fileWriter=new FileWriter(logFile,true)

        val printWriter=new PrintWriter(fileWriter)

        printWriter.println(s"[$timeStamp]")
        printWriter.println(s"User: $userInput")
        printWriter.println(s"Pet-Care-BOT: $botResponse")
        printWriter.println("*"*1000)

        printWriter.close()
}