package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.domain.model.Dish

fun Dish.toEntity() = DishEntity(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    imagePath = imagePath
)

fun DishEntity.toDomain() = Dish(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    imagePath = imagePath
)