package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.ComboDto

@Composable
fun ComboItem(
    combo: ComboDto,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = combo.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(id = com.example.myapplication.R.drawable.empty),
            error = painterResource(id = com.example.myapplication.R.drawable.empty),
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(combo.name, fontWeight = FontWeight.Bold)

            combo.description?.let {
                Text(it, fontSize = 12.sp)
            }

            Text("${combo.price}đ", color = Color.Red)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onDecrease) {
                Icon(Icons.Default.Remove, null)
            }

            Text(quantity.toString())

            IconButton(onClick = onIncrease) {
                Icon(Icons.Default.Add, null)
            }
        }
    }
}