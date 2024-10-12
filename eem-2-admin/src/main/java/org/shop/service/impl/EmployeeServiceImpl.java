package org.shop.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.MessageConstant;
import org.shop.common.constant.PasswordConstant;
import org.shop.common.constant.RedisConstant;
import org.shop.common.exception.AccountAlivedException;
import org.shop.common.exception.InvalidInputException;
import org.shop.common.utils.RegexUtil;
import org.shop.entity.Employee;
import org.shop.entity.dto.EmployeeAllDTO;
import org.shop.entity.dto.EmployeeDTO;
import org.shop.entity.dto.EmployeeLocalDTO;
import org.shop.entity.dto.EmployeeLoginDTO;
import org.shop.entity.vo.EmployeeVO;
import org.shop.mapper.EmployeeMapper;
import org.shop.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    private final StringRedisTemplate stringRedisTemplate;


    //! Func

    @Override
    public String sendCodeA(String phone, HttpSession session) {

        if (RegexUtil.isPhoneInvalid(phone)) throw new InvalidInputException(MessageConstant.PHONE_INVALID);

        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_ADMIN + phone + "*"); //删除之前的验证码
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }

        String code = RandomUtil.randomNumbers(6); //简单生成(管理员端)

        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_CODE_KEY_ADMIN + phone, code, RedisConstant.LOGIN_CODE_TTL_ADMIN, TimeUnit.MINUTES);

        return code; //调试环境: 返回验证码; 未来引入邮箱发送验证码
    }


    @Override
    @SneakyThrows
    public String loginA(EmployeeLoginDTO employeeLoginDTO, HttpSession session) {

        //删除掉之前本地的所有登陆令牌
        // ? (localhost环境) 仅本地调试时使用
        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_ADMIN + "*");
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }

        //校验手机号
        String phone = employeeLoginDTO.getPhone();
        if (RegexUtil.isPhoneInvalid(phone)) throw new InvalidInputException(MessageConstant.PHONE_INVALID);

        //从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_CODE_KEY_ADMIN + phone);
        String code = employeeLoginDTO.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) throw new InvalidInputException(MessageConstant.CODE_INVALID);

        //根据用户名查询用户
        Employee employee = query().eq("account", employeeLoginDTO.getAccount()).one();
        if (employee == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);


        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        EmployeeLocalDTO employeeLocalDTO = BeanUtil.copyProperties(employee, EmployeeLocalDTO.class);
        Map<String, Object> employeeMap = BeanUtil.beanToMap(employeeLocalDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        // 存储
        String tokenKey = RedisConstant.LOGIN_USER_KEY_ADMIN + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, employeeMap);
        stringRedisTemplate.expire(tokenKey, RedisConstant.LOGIN_USER_TTL_ADMIN, TimeUnit.MINUTES);

        return token;
    }


    @Override
    public void logoutA() {
        //删除掉之前的所有登陆令牌 (单机调试模式)
        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_ADMIN + "*");
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }
    }


    //! ADD


    @Override
    public void postEmployeeA(EmployeeDTO employeeDTO) {

        if (this.query().eq("account", employeeDTO.getAccount()).count() > 0) {
            throw new AccountAlivedException(MessageConstant.ACCOUNT_ALIVED);
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        this.save(employee);
    }


    //! DELETE


    @Override
    @SneakyThrows
    public void deleteEmployeeA(String account) {
        Employee employee = this.getOne(Wrappers.<Employee>lambdaQuery().eq(Employee::getAccount, account));
        if (employee == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        this.removeById(employee.getId());
    }


    //! UPDATE


    @Override
    @Transactional
    @SneakyThrows
    public void putEmployeeA(EmployeeAllDTO employeeAllDTO) {


        Optional<Employee> optionalEmployee = Optional.ofNullable(this.getOne(Wrappers.<Employee>lambdaQuery().eq(Employee::getAccount, employeeAllDTO.getAccount())));
        if (optionalEmployee.isEmpty()) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        // 选择性更新
        // FIXME
        Employee e2 = optionalEmployee.get(); //获取原始对象
        String[] nullPropertyNames = getNullPropertyNames(employeeAllDTO); //获取所有的空属性名
        BeanUtils.copyProperties(employeeAllDTO, e2, nullPropertyNames);

        Optional.ofNullable(employeeAllDTO.getPassword()) //手动调整密码生成
                .ifPresent(password -> e2.setPassword(DigestUtils.md5DigestAsHex(password.getBytes())));

        this.updateById(e2);
    }


    //! QUERY

    @Override
    @SneakyThrows
    public EmployeeVO getEmployeeA(String account) {
        Employee employee = this.getOne(Wrappers.<Employee>lambdaQuery().eq(Employee::getAccount, account));
        if (employee == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employee, employeeVO);
        return employeeVO;
    }


}
