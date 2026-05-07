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

case class  UserProfile(
    userName:String="User",
    petName:String="your Pet",
    petType:String="None"
)
case class ConversationState(
    history:List[InteractionEntry]=Nil,
    userPreferences:Map[String,String],
    user:UserProfile=UserProfile()
)