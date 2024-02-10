package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Api: 作⽤在Controller上，对控制器类的说明, tags="说明该类的作⽤，可以在前台界⾯上看到的注解"
 * @ApiModel: 作⽤在响应的类上，对返回响应数据的说明
 * @ApiModelProerty: 作⽤在类的属性上，对属性的说明
 * @ApiOperation: 作⽤在具体⽅法上，对API接⼝的说明 value = " "
 * @ApiParam: 作⽤在⽅法中的每⼀个参数上，对参数的属性进⾏说明 value = "" ,required = boolean
 */

@Api(tags = "这是一个api接口类")
@RequestMapping("/test")
@RestController
public class TestController {
    @ApiOperation(value = "者是一个所有异常的保底类")
    @GetMapping("/exception")
    public String testException() throws Exception {
        throw new Exception("这是⼀个Exception");
    }

    @ApiOperation("这是一个appException类")
    @RequestMapping("/appException")
    public String testApplicationException() {
        throw new ApplicationException("这是⼀个⾃定义的ApplicationException");
    }
}
