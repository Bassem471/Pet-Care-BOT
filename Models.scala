sealed trait Intent
object Intent{
    case object Greeting extends Intent
    case object AskPetQuestion extends Intent
    case object GetRecommendation extends Intent
    case class UpdatePreference(preferenceType:String,value:String) extends Intent
    case object Unknown extends Intent
}

case class PetCareTip(
    id:String,
    petType:String,
    category:String,
    content:String
)

case class InteractionEntry(
    turnNumber:Int,
    userInput:String,
    botResponse:String,
    intentDetection:Intent
)

case class ConversationState(
    history:List[InteractionEntry],
    userPreferences:Map[String,String]
)