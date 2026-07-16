import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.domain.model.FavoriteRestaurant

fun FavoriteRestaurantEntity.toDomain() = FavoriteRestaurant(
    restaurantId = restaurantId,
    buyerId = buyerId,
    restaurantName = restaurantName,
    category = category,
    address = address,
    timestamp = timestamp
)

fun FavoriteRestaurant.toEntity() = FavoriteRestaurantEntity(
    restaurantId = restaurantId,
    buyerId = buyerId,
    restaurantName = restaurantName,
    category = category,
    address = address,
    timestamp = timestamp
)