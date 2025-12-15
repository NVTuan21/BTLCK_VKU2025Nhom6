package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.btlck_ltdd_nhom6.R
import com.example.btlck_ltdd_nhom6.databinding.ActivityMainBinding

// Nếu sau này dùng Hilt, bạn cần thêm @AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kích hoạt View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Thiết lập Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        // Thiết lập Toolbar để tự động hiển thị tên Fragment hiện tại
        setupActionBarWithNavController(navController)
    }

    // Xử lý nút Back trên Toolbar (Arrow Up)
    override fun onSupportNavigateUp(): Boolean {
        // NavController sẽ tự động quay lại màn hình trước đó
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
