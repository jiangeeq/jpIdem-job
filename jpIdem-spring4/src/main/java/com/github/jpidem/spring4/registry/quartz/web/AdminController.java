package com.github.jpidem.spring4.registry.quartz.web;

import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.spring4.registry.quartz.JobStatusEnum;
import com.github.jpidem.spring4.registry.quartz.RetrySchedulerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * job管理
 *
 * @author yuni[mn960mn@163.com]
 * @see RetrySchedulerFactoryBean
 */
@RequestMapping("/job")
public class AdminController {

    public static final String RESPONSE = "success";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RetryTaskMapper retryTaskMapper;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseBody
    @GetMapping("/getList")
    public List<JobDetail> getJobList() {
        return applicationContext.getBeansOfType(RetrySchedulerFactoryBean.class).values().stream().map(this::getjobDetail).collect(Collectors.toList());
    }

    private JobDetail getjobDetail(RetrySchedulerFactoryBean bean) {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setIdentity(bean.getJobIdentity());
        jobDetail.setName(bean.getJobName() == null ? "" : bean.getJobName());
        jobDetail.setPeriod(bean.getJobPeriod());
        jobDetail.setCount(retryTaskMapper.queryRetryTotal(bean.getJobIdentity()));
        jobDetail.setStatus(bean.getJobStatusEnum().getDesc());
        if (bean.getJobStatusEnum() == JobStatusEnum.RUNNING) {
            LocalDateTime nextTime = bean.getNextTime();
            jobDetail.setNextTime(nextTime == null ? "- -" : dateTimeFormatter.format(nextTime));
        } else {
            jobDetail.setNextTime("- -");
        }
        return jobDetail;
    }

    @ResponseBody
    @PostMapping("/start")
    public String startJob(@RequestParam("identity") String identity) {
        Optional<RetrySchedulerFactoryBean> optional = applicationContext.getBeansOfType(RetrySchedulerFactoryBean.class).values()
                .stream().filter(job -> job.getJobIdentity().equals(identity)).filter(job -> job.getJobStatusEnum() != JobStatusEnum.RUNNING).findAny();
        optional.ifPresent(RetrySchedulerFactoryBean::startNow);
        return RESPONSE;
    }

    @ResponseBody
    @PostMapping("/stop")
    public String stopJob(@RequestParam("identity") String identity) {
        Optional<RetrySchedulerFactoryBean> optional = applicationContext.getBeansOfType(RetrySchedulerFactoryBean.class).values()
                .stream().filter(job -> job.getJobIdentity().equals(identity)).filter(job -> job.getJobStatusEnum() == JobStatusEnum.RUNNING).findAny();
        optional.ifPresent(RetrySchedulerFactoryBean::stop);
        return RESPONSE;
    }

    @ResponseBody
    @PostMapping("/run")
    public String runJob(@RequestParam("identity") String identity) {
        Optional<RetrySchedulerFactoryBean> optional = applicationContext.getBeansOfType(RetrySchedulerFactoryBean.class).values()
                .stream().filter(job -> job.getJobIdentity().equals(identity)).filter(job -> job.getJobStatusEnum() == JobStatusEnum.RUNNING).findAny();
        optional.ifPresent(RetrySchedulerFactoryBean::runAsync);
        return RESPONSE;
    }
}