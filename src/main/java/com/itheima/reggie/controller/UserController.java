package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.entity.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author jsc
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        String phone = user.getPhone();
        if (StringUtils.hasLength(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码是: {}",code);
//            SMSUtils.sendMessage("Cjs流流","SMS_121857515",phone,code);
            httpSession.setAttribute(phone,code);
            return R.success("验证码短信发送成功！");
        }
       return R.error("短信发送失败");

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object attribute = httpSession.getAttribute(phone);
        if (code != null && code.equals(attribute)) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("手机验证码不正确");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return R.success("退出登录");
    }


}
