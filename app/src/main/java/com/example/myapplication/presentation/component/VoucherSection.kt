package com.example.myapplication.presentation.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.myapplication.data.remote.dto.VoucherDto


@Composable
fun VoucherSection(
    vouchers: List<VoucherDto>,
    selectedVoucher: String?,
    onSelect: (String?) -> Unit
) {

    Column {

        Text("PHƯƠNG THỨC GIẢM GIÁ", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        vouchers.forEach { voucher ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(voucher.id) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = selectedVoucher == voucher.id,
                    onClick = { onSelect(voucher.id) }
                )

                Column {

                    Text(voucher.title)

                    voucher.description?.let {
                        Text(it, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}