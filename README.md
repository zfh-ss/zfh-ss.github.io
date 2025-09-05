# zfh-ss.github.io
We are!
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    幸福的开始！
</head>
<body>
    <a href="https://wx1.sinaimg.cn/orj360/006cm3Wgly1ho1dh1rcg8j30u0140n6w.jpg" target="_blank">花花🌼</a><br />
    <a href="https://wx1.sinaimg.cn/orj360/006cm3Wgly1ho1dh2ee5kj30u01hcgy7.jpg" target="_blank">教会我川渝蹲</a><br />
    <a href="https://wx3.sinaimg.cn/mw690/006cm3Wgly1ho1dh3a4k4j30u01407n2.jpg" target="_blank">海边牵手手👫</a><br />
    <a href="https://wx3.sinaimg.cn/mw690/006cm3Wgly1ho1dh117quj30k00zkgtv.jpg" target="_blank">比耶✌</a><br />
    <a href="https://wx4.sinaimg.cn/mw690/006cm3Wgly1ho1dh51cwaj30k00zk78j.jpg" target="_blank">酷酷的小细节</a><br />
    <a href="https://wx4.sinaimg.cn/mw690/006cm3Wgly1ho1dh5ujqkj30u01hc18n.jpg" target="_blank">不辣！！！🌶</a><br />
    <a href="https://wx1.sinaimg.cn/mw690/006cm3Wgly1ho1dh6pf5fj313u0tund0.jpg" target="_blank">好好吃饭</a><br />   
</body>
</html>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    幸福记录
</head>
<body>
    <a href="https://weibo.com/u/5679371384" target="_blank"><h2>幸福！</h2></a><br />
    <a href="双双.html" target="_blank" ><h3>双双大美女！</h3></a><br />
    <a href="一、幸福的开始！.html" target="_blank">一、幸福的开始！</a>
    <p>真的 一定要记住这些瞬间</p>
    <p>强烈的感觉到幸福和被爱的瞬间</p>
    <p>就算很抽象</p>
    <p>也尽量要记录和表达</p>
    <p>在爱的旅途中</p>
    <P>幸福的阈值也许会越来越高</P>
    <P>这表示我们感受到幸福的时刻</P>
    <P>也可能会随之减少</P>
    <P>至少在感受到的时候把它定格保存</P>
    <P>依然还会掉进爱的漩涡</P>
    <p>这些你都在教会我</p>
</body>
</html>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>周年纪念计时器</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: white;
            line-height: 1.6;
            overflow-x: hidden;
            background: #1a1a2e;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        header {
            text-align: center;
            padding: 30px 0;
            background: rgba(26, 26, 46, 0.85);
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        h1 {
            font-size: 2.8rem;
            margin-bottom: 15px;
            font-weight: 300;
            letter-spacing: 2px;
        }
        
        .subtitle {
            font-size: 1.2rem;
            opacity: 0.8;
            font-weight: 300;
            max-width: 600px;
            margin: 0 auto;
        }
        
        .image-container {
            position: relative;
            width: 100%;
            height: 70vh;
            margin: 40px 0;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
        }
        
        .image-container img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s ease;
        }
        
        .image-container:hover img {
            transform: scale(1.03);
        }
        
        .countdown-overlay {
            position: absolute;
            bottom: 20%;
            left: 0;
            width: 100%;
            text-align: center;
            background: rgba(0, 0, 0, 0.4);
            padding: 25px 0;
            backdrop-filter: blur(5px);
        }
        
        .countdown {
            display: flex;
            justify-content: center;
            gap: 30px;
            margin-bottom: 15px;
        }
        
        .countdown-item {
            text-align: center;
        }
        
        .countdown-number {
            font-size: 2.5rem;
            font-weight: 300;
            margin-bottom: 5px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }
        
        .countdown-label {
            font-size: 0.9rem;
            text-transform: uppercase;
            opacity: 0.8;
            letter-spacing: 1px;
        }
        
        .start-date {
            font-size: 1.2rem;
            margin-top: 15px;
            opacity: 0.9;
            font-weight: 300;
        }
        
        .message {
            text-align: center;
            max-width: 700px;
            margin: 40px auto;
            padding: 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            line-height: 1.8;
        }
        
        .instructions {
            background: rgba(26, 26, 46, 0.9);
            padding: 30px;
            border-radius: 10px;
            margin: 40px 0;
        }
        
        .instructions h2 {
            margin-bottom: 20px;
            font-weight: 400;
            color: #e94560;
        }
        
        .instructions ol {
            margin-left: 20px;
            line-height: 1.8;
        }
        
        .instructions li {
            margin-bottom: 10px;
        }
        
        footer {
            text-align: center;
            padding: 30px 0;
            margin-top: 40px;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
            font-size: 0.9rem;
            opacity: 0.7;
        }
        
        @media (max-width: 768px) {
            h1 {
                font-size: 2rem;
            }
            
            .countdown {
                gap: 15px;
            }
            
            .countdown-number {
                font-size: 1.8rem;
            }
            
            .image-container {
                height: 50vh;
            }
            
            .countdown-overlay {
                bottom: 15%;
                padding: 15px 0;
            }
        }
        
        @media (max-width: 480px) {
            .countdown {
                gap: 10px;
            }
            
            .countdown-number {
                font-size: 1.5rem;
            }
            
            .countdown-label {
                font-size: 0.7rem;
            }
            
            .start-date {
                font-size: 1rem;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1><i class="fas fa-heart"></i> 周年纪念计时器</h1>
            <p class="subtitle">记录每一个珍贵瞬间，庆祝共同走过的美好时光</p >
        </header>
        
        <div class="image-container">
            < img src="d1.jpg" alt="纪念图片">
            
            <div class="countdown-overlay">
                <div class="countdown">
                    <div class="countdown-item">
                        <div class="countdown-number" id="days">0</div>
                        <div class="countdown-label">天</div>
                    </div>
                    <div class="countdown-item">
                        <div class="countdown-number" id="hours">0</div>
                        <div class="countdown-label">时</div>
                    </div>
                    <div class="countdown-item">
                        <div class="countdown-number" id="minutes">0</div>
                        <div class="countdown-label">分</div>
                    </div>
                    <div class="countdown-item">
                        <div class="countdown-number" id="seconds">0</div>
                        <div class="countdown-label">秒</div>
                    </div>
                </div>
                <div class="start-date">起始日: 2024年3月22日</div>
            </div>
        </div>
        
        <div class="message">
            <p>在这里记录着我们从开始到现在共同度过的每一分每一秒。</p >
        </div>
        
        <footer>
            <p>周年纪念计时器 &copy; 2023 - 珍惜每一刻</p >
        </footer>
    </div>

    <script>
        // 设置起始日期
        const startDate = new Date('2024-03-22T00:00:00');
        
        function updateCountdown() {
            const now = new Date();
            const diff = now - startDate;
            
            // 计算天、时、分、秒
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((diff % (1000 * 60)) / 1000);
            
            // 更新显示
            document.getElementById('days').textContent = days;
            document.getElementById('hours').textContent = hours.toString().padStart(2, '0');
            document.getElementById('minutes').textContent = minutes.toString().padStart(2, '0');
            document.getElementById('seconds').textContent = seconds.toString().padStart(2, '0');
        }
        
        // 初始更新
        updateCountdown();
        
        // 每秒更新一次
        setInterval(updateCountdown, 1000);
    </script>
</body>
</html>
