package com.example.deadlinecty.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun giaodientesst(navController: NavHostController){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)

    ){
        chuhome()
    }
}
@Composable
fun chuhome(){
    Text(text = "Ban da dang nhap thanh cong!!")
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun manhinhtesst(){
    val navController = rememberNavController()
giaodientesst(navController)
}