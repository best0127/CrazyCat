# 围住神经猫 (CrazyCat)

一款经典的六边形网格围堵游戏。玩家通过点击空白格子放置障碍物，阻止小猫逃出地图边缘。

## 游戏截图

![截图](1.jpg)

## 玩法说明

- 点击地图中的空白位置放置障碍物
- 小猫会自动向边缘移动，尝试逃脱
- 在小猫到达边缘前将其完全围住即可获胜
- 每次启动会显示玩法提示
- 游戏结束可选择"再来一局"或返回

## 游戏模式

| 模式 | 网格大小 | 障碍数 | 颜色 | 说明 |
|------|---------|--------|------|------|
| 简单 | 8×8 | 16 | 🟢 绿色 | 新手推荐 |
| 普通 | 10×10 | 20 | 🔵 蓝色 | 适中难度 |
| 困难 | 12×12 | 24 | 🟣 紫色 | 挑战极限 |
| 限时 | 10×10 | 20 | 🟠 橙色 | 10秒倒计时 |

## 功能特性

- 🎮 四种游戏难度模式
- ⏱️ 限时模式实时倒计时（最后3秒红色警示）
- 🏆 最佳成绩记录（SharedPreferences 持久化）
- 🎨 大圆角现代 UI 设计
- 🌙 深色模式支持
- 📱 自适应图标 (Adaptive Icon)
- 🔄 游戏结束可直接重玩

## 技术栈

- **语言**: Java
- **平台**: Android (minSdk 21, targetSdk 34)
- **架构**: SurfaceView 自定义绘图 + 六边形网格算法
- **构建**: Gradle 8.3 + AGP 8.1.0

## 项目结构

```
app/src/main/java/ndnu/tdy/CreazyCat/
├── Activity/
│   ├── BaseActivity.java      # 基类，提供全屏设置
│   ├── MainActivity.java      # 启动页 + 玩法引导
│   ├── ChooseActivity.java    # 模式选择页（显示最佳成绩）
│   └── GameActivity.java      # 游戏页面
├── View/
│   ├── GameView.java          # 游戏主视图（协调层）
│   ├── GameEngine.java        # 游戏状态管理
│   ├── CatAI.java             # 猫的 AI 移动算法
│   ├── GameRenderer.java      # 渲染器（网格、动画、背景）
│   └── Point.java             # 网格单元格数据类
└── GamePreferences.java       # 数据持久化（成绩、设置）
```

## 构建运行

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 运行单元测试
./gradlew test

# 安装到设备
./gradlew installDebug
```

## 许可证

仅供学习交流使用。
