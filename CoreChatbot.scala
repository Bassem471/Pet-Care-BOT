import CoreChatBot.greetUser
object CoreChatBot{
    def greetUser():String=
        "I am your Pet-Care-Bot.\n " +
        "\tI can give you advice on dogs, cats, fish, and birds.\n " +
        "\t\t\t\t\t\t How can I help you Today?"

    def parseInput(input:String):List[String]=
        input.toLowerCase.replaceAll("[^a-z0-9\\s]","").split("\\s+").toList

    def detectIntent(tokens:List[String]):Intent=
        if(tokens.contains("recommend")||tokens.contains("suggest"))
            Intent.GetRecommendation
        else if (tokens.intersect(List("feed","diet","train","sick","health","dog","fish","cat","bird")).nonEmpty)
            Intent.AskPetQuestion
        else if(tokens.contains("hi")||tokens.contains("hello"))
            Intent.Greeting
        else
            Intent.Unknown

    def handleUserInput(input:String):String=
        val badWords=List("kill","hurt","harm","abuse","poison","hit")
        if(badWords.exists(word=>input.toLowerCase.contains(word)))
            "I am a Pet Care BOT, I can't provide these informations\n"
        else{
        val tokens=parseInput(input)
        val userIntent=detectIntent(tokens)
        userIntent match
            case Intent.Greeting=>greetUser()

            case Intent.AskPetQuestion=>generateResponse(input)

            case Intent.GetRecommendation=>"[person 2 module]"

            case Intent.UpdatePreference(prefType,value)=>"[person 3 module]"

            case Intent.Unknown=>"I'm sorry, I didn't quite catch that.\n " +
              "\tTry asking for recommendations or asking 'what should I feed my pet?'"}    
    
    def generateResponse(query:String):String=
        val tokens=parseInput(query)

        val mentionedPet=tokens.find(word=>List("dog","cat","fish","bird").contains(word))

        mentionedPet match
            case Some(pet)=>
                val allTips=KnowledgeBase.petTips.filter(tip=>tip.petType.toLowerCase==pet)

                val matchingTip=
                    if(tokens.intersect(List("feed","diet","eat")).nonEmpty)
                        allTips.filter(tip=>tip.category.toLowerCase=="diet")
                    else if(tokens.intersect(List("train","training")).nonEmpty)
                        allTips.filter(tip=>tip.category.toLowerCase=="training")
                    else if(tokens.intersect(List("sick","health")).nonEmpty)
                        allTips.filter(tip=>tip.category.toLowerCase=="health")
                    else
                        allTips

                if(matchingTip.nonEmpty)
                    val randomIndex=scala.util.Random.nextInt(matchingTip.length)
                    val selectedTip=matchingTip(randomIndex)
                    s"Here is a ${selectedTip.category} tip for your $pet:${selectedTip.content}"
                else
                    s"I don't have any specific tips for  a $pet right now."
        
            case None =>"that's a great question, but I need to know what kind of pet you have!\n " +
              "Is it a dog, cat, fish or bird?"
}