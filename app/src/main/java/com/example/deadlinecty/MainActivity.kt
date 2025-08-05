package com.example.deadlinecty

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deadlinecty.cty2.HomeScreenMess
import com.example.deadlinecty.ui.theme.DeadlineCTYTheme
import com.example.deadlinecty.ui.theme.Homequenmatkhau
import com.example.deadlinecty.util.getConfig
import com.example.deadlinecty.util.getSession
import com.example.deadlinecty.util.login
import com.example.deadlinecty2.data.HomeScreenTN
import com.example.deadlinecty2.data.HomeViewModel
import com.example.deadlinecty2.data.MessageViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val viewModel: HomeViewModel = viewModel()
            val messageViewModel: MessageViewModel = viewModel()
            DeadlineCTYTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("quenmatkhau") {
                            Homequenmatkhau(navController)
                        }
                        composable("chuyensanggroupmess"){
                            HomeScreenMess(navController, viewModel = viewModel)
                        }
                        composable("groupChatDetail/{groupId}") { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                            val groupChat = viewModel.groupList.find { it.conversationId == groupId }

                            if (groupChat != null) {
                                HomeScreenTN(
                                    groupChat = groupChat,
                                    navController = navController,
                                    messageViewModel = messageViewModel)
                            } else {
                                Text(text = "Nhóm chat không tồn tại")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
fun HomeScreen(navController: NavHostController) {
        val openDialog = remember { mutableStateOf(false) }
        val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundHome()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
        ) {
            LogoHome()
            DialogThongBaoThanhCong(openDialog, navController, context)
            NhapThongTin(navController, openDialog)
        }
    }
}

    @Composable
    fun DialogThongBaoThanhCong(openDialog: MutableState<Boolean>, navController: NavHostController, context: Context) {
        if (openDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Thông báo") },
                text = { Text("Đăng nhập thành công!") },
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = {
                            openDialog.value = false
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(android.Manifest.permission.CAMERA),
                                    100
                                )
                            }
                            navController.navigate("chuyensanggroupmess")

                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }



    @Composable
fun NhapThongTin(navController: NavHostController, openDialog: MutableState<Boolean>){
    val tencty = remember { mutableStateOf("") }
    val sdt = remember { mutableStateOf("") }
    val matkhau = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
//    val isPhoneValid = sdt.value.matches(Regex("^0\\d{9}$"))
//    val hasLetter = matkhau.value.any { it.isLetter() }
//    val hasDigit = matkhau.value.any { it.isDigit() }
//    val dodaimk = matkhau.value.length >= 8
//    val chichuvaso = matkhau.value.all {it.isLetterOrDigit()}
//    val isPasswordValid = hasLetter && hasDigit && chichuvaso && dodaimk
        val context = LocalContext.current


        Column(horizontalAlignment = Alignment.CenterHorizontally){
        OutlinedTextField(
            value = tencty.value,
            onValueChange = {tencty.value = it},
            label = {Text("Tên công ty")},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "hinh ten cty")
            },
            shape = RoundedCornerShape(15.dp)
        )
        // Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = sdt.value,
            onValueChange = {sdt.value = it},
            label = {Text("Số điện thoại")},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "hinh tk")
                          },
            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number),
            //isError = sdt.value.isNotEmpty() && !sdt.value.matches(Regex("^\\d{10}$")),
            modifier = Modifier.padding(top = 8.dp)
        )

        //val passwordVisibility = remember { mutableStateOf(false) };
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = matkhau.value,
            onValueChange = { matkhau.value = it },
            label = { Text("Mật Khẩu") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Biểu tượng khóa"
                )
            },
            shape = RoundedCornerShape(15.dp),
            visualTransformation = if (passwordVisibility.value) {
                androidx.compose.ui.text.input.VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val icon = if (passwordVisibility.value) {
                    painterResource(id = R.drawable.ic_eye_on_bold)
                } else {
                    painterResource(id = R.drawable.ic_eye_off_bold)
                }
                val description = if (passwordVisibility.value) "Ẩn mật khẩu" else "Hiện mật khẩu"

                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value
                }) {
                    Icon(
                        painter = icon,
                        contentDescription = description
                    )
                }
            },
            //isError = matkhau.value.isNotEmpty() && !(matkhau.value.any { it.isLetter() } && matkhau.value.any { it.isDigit() } && dodaimk)
        )

        Spacer(modifier = Modifier.height(16.dp))

//        val isFormValid = isPhoneValid && isPasswordValid
        val isFormValid = true

        androidx.compose.material3.Button(
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            onClick = {
                if (isFormValid) {
                    getSession(context){
                        getConfig(context, sdt.value) {
                            login(context, sdt.value, matkhau.value){
                                openDialog.value = true
                            }
                        }
                    }
                } else {
                    Log.e("Form", "Thông tin chưa hợp lệ")
                }
            },
            //enabled = isFormValid,
            ){
            Text("Đăng Nhập")
        }
        Text(
            text = "Quên mật khẩu",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                navController.navigate("quenmatkhau")
                }
            )
        }
    }
}



@Composable
fun BackgroundHome() {
    Image(painterResource(id = R.drawable.bg_login),
        contentDescription = "logo home",
        modifier = Modifier.fillMaxSize(),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop)
}
@Composable
fun LogoHome(){
    Image(painterResource(id = R.drawable.ic_techres),
        contentDescription = "logo homee",
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
        )
}
fun encodePasswordBase64(password: String): String {
    val data = password.toByteArray(Charsets.UTF_8)
    return Base64.encodeToString(data, Base64.NO_WRAP)
}




