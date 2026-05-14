"""
一次性种子数据脚本：通过后端 API 插入 50 用户 + 50 活动 + 报名关系
用法: python scripts/seed_data.py
"""
import json
import urllib.request
import urllib.error
import http.cookiejar
import random
from datetime import datetime, timedelta

BASE = "http://localhost:8080/api/v1"
PASSWORD = "123456"

# ============================================================
# 50 个现实用户
# ============================================================
USERS = [
    ("张伟","zhangwei",28,"M","zhangwei@qq.com"),
    ("李娜","lina",25,"F","lina@163.com"),
    ("王磊","wanglei",31,"M","wanglei@gmail.com"),
    ("赵敏","zhaomin",27,"F","zhaomin@qq.com"),
    ("刘洋","liuyang",24,"M","liuyang@outlook.com"),
    ("陈静","chenjing",29,"F","chenjing@126.com"),
    ("杨帆","yangfan",26,"M","yangfan@qq.com"),
    ("黄丽","huangli",33,"F","huangli@gmail.com"),
    ("周强","zhouqiang",30,"M","zhouqiang@163.com"),
    ("吴婷","wuting",22,"F","wuting@qq.com"),
    ("徐明","xuming",35,"M","xuming@outlook.com"),
    ("孙悦","sunyue",23,"F","sunyue@126.com"),
    ("马超","machao",28,"M","machao@qq.com"),
    ("朱红","zhuhong",32,"F","zhuhong@gmail.com"),
    ("胡涛","hutao",27,"M","hutao@163.com"),
    ("郭琳","guolin",26,"F","guolin@qq.com"),
    ("何军","hejun",34,"M","hejun@outlook.com"),
    ("林芳","linfang",25,"F","linfang@126.com"),
    ("罗浩","luohao",29,"M","luohao@qq.com"),
    ("梁雪","liangxue",24,"F","liangxue@gmail.com"),
    ("郑飞","zhengfei",31,"M","zhengfei@163.com"),
    ("谢雨","xieyu",22,"F","xieyu@qq.com"),
    ("韩冰","hanbing",36,"M","hanbing@outlook.com"),
    ("唐琪","tangqi",21,"F","tangqi@126.com"),
    ("冯刚","fenggang",33,"M","fenggang@qq.com"),
    ("董洁","dongjie",28,"F","dongjie@gmail.com"),
    ("程亮","chengliang",26,"M","chengliang@163.com"),
    ("曹颖","caoying",30,"F","caoying@qq.com"),
    ("邓鹏","dengpeng",25,"M","dengpeng@outlook.com"),
    ("彭蕾","penglei",27,"F","penglei@126.com"),
    ("萧然","xiaoran",24,"M","xiaoran@qq.com"),
    ("蒋欣","jiangxin",32,"F","jiangxin@gmail.com"),
    ("沈逸","shenyi",29,"M","shenyi@163.com"),
    ("姚思","yaosi",23,"F","yaosi@qq.com"),
    ("卢伟","luwei",35,"M","luwei@outlook.com"),
    ("崔晴","cuiqing",26,"F","cuiqing@126.com"),
    ("苏力","suli",31,"M","suli@qq.com"),
    ("魏然","weiran",21,"F","weiran@gmail.com"),
    ("蔡恒","caiheng",28,"M","caiheng@163.com"),
    ("潘悦","panyue",25,"F","panyue@qq.com"),
    ("杜海","duhai",34,"M","duhai@outlook.com"),
    ("任燕","renyan",22,"F","renyan@126.com"),
    ("姜龙","jianglong",27,"M","jianglong@qq.com"),
    ("钟灵","zhongling",30,"F","zhongling@gmail.com"),
    ("廖志","liaozhi",33,"M","liaozhi@163.com"),
    ("邱莹","qiuying",24,"F","qiuying@qq.com"),
    ("金勇","jinyong",29,"M","jinyong@outlook.com"),
    ("陆薇","luwei2",26,"F","luwei2@126.com"),
    ("叶峰","yefeng",32,"M","yefeng@qq.com"),
    ("夏荷","xiahe",20,"F","xiahe@gmail.com"),
]

# ============================================================
# 50 个现实活动
# ============================================================
ACTIVITIES = [
    ("周末篮球3v3对抗赛","每周六下午的篮球对抗赛，3v3半场制。欢迎各水平的球友参加！","北京市朝阳区奥体中心篮球场",39.993,116.395,6,12),
    ("晨跑小分队——朝阳公园","工作日早上7点晨跑，5公里轻松跑配速6分半。跑完一起早餐。","北京市朝阳区朝阳公园南门",39.935,116.475,2,8),
    ("足球爱好者联赛（周三夜场）","8人制草坪足球赛，已有固定队伍。新手老手都欢迎。","北京市海淀区中关村体育场",39.983,116.312,14,22),
    ("羽毛球双打交流赛","双打循环赛，提供羽毛球。请自带球拍穿着运动鞋入场。","上海市浦东新区源深体育馆",31.230,121.535,4,16),
    ("瑜伽冥想——周末放松","户外草坪瑜伽，适合零基础入门。请自带瑜伽垫。","深圳市南山区深圳湾公园",22.518,113.956,3,15),
    ("攀岩初体验","室内攀岩馆体验课程，教练指导，含装备租赁。","广州市天河区体育中心攀岩馆",23.131,113.325,3,8),
    ("骑行京郊——十三陵水库","全程约60公里，中等强度。沿途风景优美，中午在农家院午餐。","北京市昌平区十三陵水库",40.253,116.234,4,12),
    ("游泳训练营","针对自由泳和蛙泳的技术训练。需有基本游泳能力。","杭州市西湖区黄龙体育中心游泳馆",30.269,120.132,2,10),
    ("春日徒步——香山","香山登山徒步，全程约8公里。难度适中，沿途赏花。","北京市海淀区香山公园",39.996,116.183,3,20),
    ("乒乓球擂台赛","单打淘汰制，三局两胜。业余爱好者交流为主。","成都市武侯区省体育馆乒乓馆",30.638,104.067,4,16),
    ("《深入理解计算机系统》读书会","每周一章，轮流分享。本期讨论第5章：优化程序性能。","北京市海淀区五道口PageOne书店",39.993,116.338,3,12),
    ("Python数据分析实战工作坊","从pandas到matplotlib，手把手做数据分析项目。请自带笔记本电脑。","上海市徐汇区交大慧谷",31.202,121.439,4,15),
    ("英语角——Topic: AI & Future","全英文讨论人工智能对未来的影响。欢迎各水平英语爱好者。","深圳市福田区中心书城",22.547,114.059,3,20),
    ("考研数学互助小组","高数+线代+概率论，每周集中刷题，互帮互助。","武汉市洪山区武汉大学图书馆",30.538,114.362,4,12),
    ("日语入门——五十音到简单会话","面向零基础的日语学习活动，日语专业研究生主讲。","南京市鼓楼区南京大学附近咖啡馆",32.058,118.777,2,10),
    ("LeetCode周赛复盘","一起复盘上周LeetCode周赛题目，分享解题思路和优化技巧。","线上——腾讯会议",None,None,3,15),
    ("摄影基础——构图与光线","理论讲解+户外实拍练习。请自带相机或手机。","杭州市西湖区西湖景区",30.245,120.138,4,12),
    ("书法体验课——楷书入门","笔墨纸砚已备好。从基本笔画教起，体验传统文化之美。","西安市碑林区书院门",34.255,108.946,2,8),
    ("区块链与Web3技术沙龙","讨论DeFi、NFT、DAO等Web3前沿话题，有行业从业者分享。","北京市朝阳区望京SOHO",40.001,116.483,5,25),
    ("GRE词汇速记法分享会","分享词根词缀记忆法，帮助高效记忆GRE词汇。","广州市天河区华南理工大学",23.154,113.346,2,10),
    ("狼人杀之夜","12人标准局，有上帝主持。新手可以玩一两局学习规则。","北京市海淀区中关村创业大街",39.987,116.311,8,14),
    ("周末桌游聚会——卡坦岛+璀璨宝石","经典德式桌游局，规则简单容易上手。提供零食饮料。","上海市静安区南京西路桌游吧",31.230,121.455,3,8),
    ("KTV周五嗨唱","流行金曲+怀旧经典，麦霸快来！AA制。","深圳市南山区海岸城KTV",22.518,113.938,4,10),
    ("火锅聚餐——重庆老火锅","周五晚上约火锅，聊聊一周趣事。人均80。","成都市锦江区春熙路",30.655,104.075,4,12),
    ("户外烧烤+露营","周六下午出发，晚上烧烤+篝火，周日返回。帐篷可租。","北京市怀柔区雁栖湖",40.372,116.658,4,16),
    ("电影之夜——《肖申克的救赎》","经典电影重温+映后讨论。提供爆米花。","杭州市西湖区某私人影院",30.274,120.128,3,12),
    ("桌球友谊赛","美式8球，循环赛制。球技不论高低，开心就好。","广州市越秀区北京路桌球城",23.125,113.266,2,8),
    ("密室逃脱——古墓主题","4-8人密室逃脱，中等难度。考验团队协作和逻辑推理。","武汉市江汉区江汉路",30.581,114.292,4,8),
    ("陶艺DIY体验","拉坯+彩绘，每人可做一件作品带回家。零基础可参加。","厦门市思明区曾厝垵",24.440,118.103,2,8),
    ("脱口秀开放麦之夜","每周三的脱口秀开放麦，欢迎上台表演或作为观众。","北京市东城区鼓楼东大街",39.941,116.403,5,30),
    ("棋牌之夜——斗地主+麻将","轻松娱乐，纯娱乐不涉及现金。","重庆市渝中区解放碑",29.562,106.574,3,8),
    ("城市漫步——上海老城厢","从老西门到城隍庙，探寻上海老城厢的历史建筑和美食。","上海市黄浦区老西门",31.220,121.485,2,15),
    ("品茶会——岩茶品鉴","大红袍、水仙、肉桂。茶艺师带领品鉴，了解岩茶文化。","福州市鼓楼区三坊七巷",26.081,119.296,2,8),
    ("周末早午餐——Brunch聚会","慵懒的周末从早午餐开始。一起享受美食和阳光。","广州市天河区珠江新城",23.122,113.329,2,10),
    ("公益净滩行动","清理海滩垃圾，保护海洋环境。提供垃圾袋和手套。","青岛市崂山区石老人海滩",36.097,120.477,5,30),
    ("水彩画写生——植物园","户外水彩写生。基础颜料和画纸已备，你只需来画。","昆明市盘龙区昆明植物园",25.141,102.742,2,10),
    ("吉他弹唱入门","从C大调基本和弦开始，学弹一首简单歌曲。请自带吉他。","北京市朝阳区798艺术区",39.985,116.495,2,8),
    ("周末街舞课——Hip-Hop基础","专业舞者授课，从基本律动和步伐开始。穿着宽松运动服。","成都市武侯区九眼桥",30.644,104.081,4,15),
    ("胶片摄影扫街——老城区","带上你的胶片机，一起扫街。暗房冲洗体验另约。","上海市虹口区多伦路",31.261,121.484,2,6),
    ("诗歌朗诵沙龙","每人准备1-2首喜欢的诗，原创或经典均可。","南京市玄武区先锋书店",32.057,118.776,3,12),
    ("Ukulele尤克里里体验","提供尤克里里，一小时学会弹唱一首歌。","厦门市思明区沙坡尾",24.445,118.081,2,10),
    ("雕塑体验——泥塑人像","用陶泥捏制人像半身像，老师全程指导。作品可烧制带走。","北京市通州区宋庄艺术区",39.950,116.720,2,6),
    ("音乐Jam即兴合奏","带上你的乐器（吉他、口琴、手鼓等），一起即兴合奏。观众也可。","广州市海珠区太古仓",23.098,113.268,3,12),
    ("创业路演——种子轮到A轮","5个创业项目路演，投资人点评。欢迎创业者和投资人参加。","深圳市南山区科技园",22.536,113.956,10,40),
    ("黑客马拉松——48小时编程挑战","组队参赛，48小时完成一个完整项目。主题：AI for Good。","杭州市余杭区阿里巴巴西溪园区",30.280,120.024,20,60),
    ("产品经理交流会","讨论需求分析、用户研究、产品设计。有3年+经验的PM优先。","北京市海淀区中关村软件园",40.050,116.287,4,15),
    ("开源之夜——贡献你的第一个PR","手把手教你给开源项目提Pull Request。请自带电脑。","上海市杨浦区大学路",31.300,121.509,5,20),
    ("自动驾驶技术研讨会","讨论感知、规划、控制三大模块的最新技术进展。","北京市海淀区清华科技园",39.997,116.334,5,20),
    ("独立开发者线下聚会","分享独立开发经验和踩坑心得。欢迎展示你的项目。","成都市高新区天府软件园",30.545,104.060,3,15),
    ("量化交易策略分享","讨论CTA、统计套利等策略。有实盘经验的优先。","上海市浦东新区陆家嘴",31.237,121.502,3,10),
]

# ============================================================
# HTTP helpers
# ============================================================
def new_opener():
    cj = http.cookiejar.CookieJar()
    return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

def api_post(op, path, data=None):
    url = f"{BASE}{path}"
    body = json.dumps(data).encode("utf-8") if data else None
    req = urllib.request.Request(url, data=body, method="POST")
    req.add_header("Content-Type", "application/json")
    try:
        resp = op.open(req, timeout=15)
        result = json.loads(resp.read().decode())
        return result.get("code", 500), result
    except urllib.error.HTTPError as e:
        err = e.read().decode() if e.fp else str(e)
        return e.code, err
    except Exception as e:
        return -1, str(e)

def api_get(op, path):
    url = f"{BASE}{path}"
    req = urllib.request.Request(url, method="GET")
    try:
        resp = op.open(req, timeout=10)
        return json.loads(resp.read().decode())
    except Exception:
        return None

# ============================================================
def main():
    random.seed(42)
    now = datetime.now()

    print("=" * 60)
    print("MeetSpace 种子数据生成器")
    print("=" * 60)

    # ---- Step 1: 注册用户 ----
    print("\n[1/3] 注册 50 个用户...")
    user_sessions = {}  # index -> opener
    user_ids = {}       # index -> user_id

    for i, (nick, uname, age, gender, email) in enumerate(USERS):
        op = new_opener()
        code, resp = api_post(op, "/auth/register",
                              {"nickname": nick, "username": uname, "password": PASSWORD})
        # 200=成功, 400=可能已存在(重复运行)
        if code not in (200, 400):
            print(f"  ! 注册 {uname} 异常: {code}")
            continue

        code, resp = api_post(op, "/auth/login",
                              {"username": uname, "password": PASSWORD})
        if code != 200:
            print(f"  ! 登录 {uname} 失败: {code}")
            continue

        uid = resp.get("data", {}).get("id")
        if uid:
            user_ids[i] = uid
            user_sessions[i] = op

        # 更新资料
        api_post(op, "/users/me/profile",
                 {"age": age, "gender": gender, "email": email})

    print(f"  完成: {len(user_ids)} 个用户已就绪")

    # ---- Step 2: 每人创建一个活动 ----
    print("\n[2/3] 创建 50 个活动...")
    activity_ids = []

    for i in range(50):
        if i not in user_sessions:
            continue
        op = user_sessions[i]
        title, desc, addr, lat, lng, minp, maxp = ACTIVITIES[i]

        start = now + timedelta(days=random.randint(2, 35), hours=random.randint(8, 20))
        # 对齐到整点
        start = start.replace(minute=0, second=0, microsecond=0)
        duration = random.choice([1.5, 2, 2.5, 3, 4, 6, 8])
        end = start + timedelta(hours=duration)
        deadline = start - timedelta(hours=random.randint(2, 48))

        payload = {
            "title": title,
            "description": desc,
            "address": addr,
            "startTime": start.strftime("%Y-%m-%dT%H:%M:%S"),
            "endTime": end.strftime("%Y-%m-%dT%H:%M:%S"),
            "signupDeadline": deadline.strftime("%Y-%m-%dT%H:%M:%S"),
            "minParticipants": minp,
            "maxParticipants": maxp,
        }
        if lat is not None:
            payload["latitude"] = lat
        if lng is not None:
            payload["longitude"] = lng

        code, resp = api_post(op, "/activities/create", payload)
        if code == 200:
            activity_ids.append(i)
        else:
            print(f"  ! 创建活动失败 [{i}] {title}: {code}")

    print(f"  完成: {len(activity_ids)} 个活动已创建")

    # ---- Step 3: 获取所有活动的实际 ID ----
    print("\n[3/3] 用户相互报名...")
    first_op = user_sessions.get(0)
    if not first_op:
        print("  错误: 没有可用 session")
        return

    code, resp = api_post(first_op, "/activities/search",
                          {"startTime": (now - timedelta(days=365)).strftime("%Y-%m-%dT%H:%M:%S"),
                           "endTime": (now + timedelta(days=365)).strftime("%Y-%m-%dT%H:%M:%S")})
    all_activities = resp.get("data", []) if code == 200 else []
    print(f"  搜索到 {len(all_activities)} 个活动")

    signup_count = 0
    for act in all_activities:
        act_id = act["id"]
        # 每个活动随机 2~6 人报名（不含创建者）
        n = min(random.randint(2, 6), len(user_sessions) - 1)
        candidates = list(user_sessions.keys())
        random.shuffle(candidates)

        signed = 0
        for user_idx in candidates:
            if signed >= n:
                break
            op = user_sessions[user_idx]
            code, resp = api_post(op, f"/activities/{act_id}/participants")
            if code == 200:
                signup_count += 1
                signed += 1
            # 400 = 已报名/名额满/截止, 跳过

    print(f"  完成: {signup_count} 条报名记录")

    # ---- 总结 ----
    print(f"\n{'=' * 60}")
    print(f"数据插入完成!")
    print(f"  user 表:                ~{len(user_ids)} 条")
    print(f"  activity 表:           {len(all_activities)} 条")
    print(f"  activity_participant 表: ~{len(all_activities) + signup_count} 条 (创建者+报名)")
    print(f"{'=' * 60}")


if __name__ == "__main__":
    main()
