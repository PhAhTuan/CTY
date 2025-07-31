package com.example.deadlinecty.ui.theme



import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.deadlinecty.R


@Composable
fun Homequenmatkhau(navController: NavHostController) {
    var matkhauhientai = remember { mutableStateOf("") }
    var nhaplaimatkhau = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        hinhlogohome2()
        //Spacer(modifier = Modifier.height(10.dp))
        Text(text = " Lấy mật khẩu", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = matkhauhientai.value,
            onValueChange = {matkhauhientai.value = it},
            label = {Text("Tài khoản hiện tại")},
            shape = RoundedCornerShape(12.dp),
            )
        OutlinedTextField(
            value = nhaplaimatkhau.value,
            onValueChange = {nhaplaimatkhau.value = it},
            label = {Text("Mật khẩu cũ")},
            shape = RoundedCornerShape(12.dp),
        )
Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            onClick = {

            }
        ) {
           Text("Lấy mật khẩu")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Quay về đăng nhập",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.popBackStack()
            }
        )
    }
}
@Composable
fun hinhlogohome2(){
    Image(painterResource(id = R.drawable.ic_techres), contentDescription = "logo home 2",
    modifier = Modifier
        .width(250.dp)
        .height(250.dp)
    )
}

/*
@Preview (showBackground = true, showSystemUi = false)
@Composable
fun DefaultPreview(){
    val navController = rememberNavController()
    DeadlineCTYTheme {
        Homequenmatkhau(navController)
    }
}
 */