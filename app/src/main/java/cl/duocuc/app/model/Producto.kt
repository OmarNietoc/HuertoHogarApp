package cl.duocuc.app.model

import androidx.annotation.DrawableRes
import cl.duocuc.app.R


data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    @DrawableRes val imagenRes: Int,
    val unid: String,
    val oferta: String? = null
)

val productosDemo = listOf(
            Producto(
                id = "FR001",
                nombre = "Manzanas Fuji",
                descripcion = "Crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.",
                precio = 1200,
                categoria = "frutas",
                imagenRes = R.drawable.apples, // 🔹 usa tu drawable local
                oferta = "Oferta",
                unid = "kg"
            ),
    Producto(
        id = "FR002",
        nombre = "Naranjas Valencia",
        descripcion = "Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes. Cultivadas en condiciones climáticas óptimas.",
        precio = 1000,
        categoria = "frutas",
        imagenRes = R.drawable.oranges,
        unid = "kg"
    ),
    Producto(
        id = "FR003",
        nombre = "Plátanos Cavendish",
        descripcion = "Maduros y dulces, perfectos para el desayuno o como snack energético. Ricos en potasio y vitaminas.",
        precio = 800,
        categoria = "frutas",
        imagenRes = R.drawable.bananas,
        unid = "kg"
    ),
    Producto(
        id = "VR001",
        nombre = "Zanahorias Orgánicas",
        descripcion = "Cultivadas sin pesticidas en la Región de O'Higgins. Excelente fuente de vitamina A y fibra, ideales para ensaladas y jugos.",
        precio = 900,
        categoria = "verduras",
        imagenRes = R.drawable.carrots,
        unid = "kg"
    ),
    Producto(
        id = "VR002",
        nombre = "Espinacas Frescas",
        descripcion = "Frescas y nutritivas, perfectas para ensaladas y batidos verdes. Cultivadas bajo prácticas orgánicas que garantizan su calidad.",
        precio = 700,
        categoria = "verduras",
        imagenRes = R.drawable.spinach,
        oferta = "Nuevo",
        unid = "kg"
    ),
    Producto(
        id = "VR003",
        nombre = "Pimientos Tricolores",
        descripcion = "Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos. Ricos en antioxidantes y vitaminas.",
        precio = 1500,
        categoria = "verduras",
        imagenRes = R.drawable.peppers,
        oferta = "Nuevo",
        unid = "kg"
    ),
    Producto(
        id = "PO001",
        nombre = "Miel Orgánica",
        descripcion = "Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable.",
        precio = 5000,
        categoria = "organicos",
        imagenRes = R.drawable.honey,
        unid = "500g"
    ),
    Producto(
        id = "PO003",
        nombre = "Quinua Orgánica",
        descripcion = "Quinua orgánica de alta calidad, rica en proteínas y nutrientes esenciales. Perfecta para una alimentación saludable.",
        precio = 3500,
        categoria = "organicos",
        imagenRes = R.drawable.quinoa,
        unid = "kg"
    ),
    Producto(
        id = "PL001",
        nombre = "Leche Entera",
        descripcion = "Leche entera fresca de vacas criadas en praderas naturales. Rica en calcio y vitaminas esenciales.",
        precio = 1800,
        categoria = "lacteos",
        imagenRes = R.drawable.milk,
        unid = "L"
    )
)
