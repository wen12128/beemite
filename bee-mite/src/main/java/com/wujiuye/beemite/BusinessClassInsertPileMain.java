/**
 * Copyright [2019-2020] [wujiuye]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wujiuye.beemite;

import com.wujiuye.beemite.ipevent.InsertPileManager;
import com.wujiuye.beemite.logimpl.DefaultBusinessCallLinkLog;
import com.wujiuye.beemite.logimpl.DefaultFuncRuntimeLog;

import java.lang.instrument.Instrumentation;

/**
 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * <p>
 * 微信公众号：Java艺术
 * QQ邮箱：419611821@qq.com
 * 微信号：ye_shao_ismy
 * <p>
 * ======================^^^^^^^==============^^^^^^^============
 *
 * @ 作者       |   吴就业 www.wujiuye.com
 * ======================^^^^^^^==============^^^^^^^============
 * @ 创建日期      |   Created in 2018年12月10日
 * ======================^^^^^^^==============^^^^^^^============
 * @ 所属项目   |   BeeMite
 * ======================^^^^^^^==============^^^^^^^============
 * @ 类功能描述    | 
 *     javaagent入口类
 *     像java程序执行入口一样需要定义public static void main方法
 *     javaagent也要有入口方法，定义一个public static void premain方法
 *    
 *     使用：
 *      先执行maven package进行打包，获取jar包的绝对路径
 *      不建议在当前项目测试
 *      在测试的web项目下点击锤子->Edit Config....-> VM options ->输入下面内容
 *          -javaagent:/MyProjects/asm-aop/insert-pile/target/insert-pile-1.0-SNAPSHOT.jar=com.wujiuye
 *      等号后面是参数
 *      如果报如下异常：
 *          java.lang.VerifyError: Expecting a stackmap frame at branch target 18
 *          jdk1.8可以添加参数：-noverify
 *          最终：
 *          -noverify -javaagent:/MyProjects/asm-aop/insert-pile/target/insert-pile-1.0-SNAPSHOT.jar=com.wujiuye
 *    
 *      spring项目中也能用，虽然spring会使用它生成的代理对象，但是最终也会调用原本的对象的方法
 * ======================^^^^^^^==============^^^^^^^============
 * @ 版本      |   ${1.0-SNAPSHOT}
 * ======================^^^^^^^==============^^^^^^^============
 */
public class BusinessClassInsertPileMain {

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // 使用注意：
    //      1.由于打jar包不会将依赖的asm的jar包也添加进去，所以在使用的项目中需要添加asm的依赖，也可以修改本项目的amven配置，
    //      将asm的jar包打包进去。
    //      2.我为了过滤掉get和set方法，所以方法名以get或者set方法开头的都不会进行插桩
    //      3.进行插桩的方法的参数不能含有基本数据类型，如果需要请以Integer等类型
    //      4.为了获取到sessionid，将事件串起来，所以此项目只适合用在web项目
    // 缺陷：
    //      1：因为要获取方法参数，而如果方法参数中有基本数据类型的参数，那么就会异常，所以方法参数只能是Object类型，即引用类型
    //      2: 需要同时添加-noverify参数，否则修改后的字节码没问题，但是会验证失败
    //
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * javaagent 的入口方法
     *
     * @param agentOps        java -javaagent:jar文件的位置[=传入premain的参数]
     * @param instrumentation
     */
    public static void premain(String agentOps, Instrumentation instrumentation) {
        System.out.println("=========================== javaagent入口 ===========================");
        //设置事件日记处理实例
        InsertPileManager.getInstance().addBusinessCallLinkLog(new DefaultBusinessCallLinkLog());
        InsertPileManager.getInstance().addFuncRuntimeLog(new DefaultFuncRuntimeLog());
        //由参数agentOps控制要拦截的包名
        //只有是该包下的类或者是其子包下的类，都进行插桩
        System.out.println("参数为："+agentOps);
        instrumentation.addTransformer(new BusinessClassInstrumentation(agentOps));
        System.out.println("=========================== javaagent 入口执行完毕 ===========================");
    }

}
