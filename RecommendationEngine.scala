object RecommendationEngine{
    def getUserPreferences(context:ConversationState):Map[String, String]=
        context.userPreferences

    def updatePreferences(key:String, value:String, currentPreferences:Map[String,String]):Map[String,String]=
        currentPreferences + (key->value)
    
    def recommend(preferences: Map[String, String], data: List[PetCareTip]): List[PetCareTip] = {
    val filtered = data.filter { tip =>
      val petMatch = preferences.get("favorite_pet") match {
        case Some(pet) => tip.petType.toLowerCase == pet.toLowerCase
        case None => true
      }
      
      val categoryMatch = preferences.get("category") match {
        case Some(cat) => tip.category.toLowerCase == cat.toLowerCase
        case None => true    
      }

      petMatch && categoryMatch
    }
    filtered
  }

    def explainRecommendation(item: PetCareTip, preferences:Map[String, String]): String=
        val petPart=preferences.get("favorite_pet") match{
            case Some(pet)=>s"your $pet"
            case None=>s"${item.petType.toLowerCase}s"
        }

        val categoryPart=preferences.get("category") match{
            case Some(cat)=>s"you interested in $cat"
            case None=>s"${item.category.toLowerCase}"
        }
        s"this ${item.category.toLowerCase} tip is perfect for $petPart based on $categoryPart."
}