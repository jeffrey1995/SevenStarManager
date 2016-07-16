package com.yuntongxun.ecdemo.health;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;

import java.util.HashMap;
import java.util.Map;

public class IncomeDetailsActivity extends BaseActivity
{
    private TextView baseSalary;
    private TextView performSalary;
    private TextView commiSalary;
    private TextView penalty;
    private TextView bonus;
    private TextView salary;
    private TextView realPay;
    private TextView socialInsurance;
    private TextView healthInsurance;
    private TextView accumulationFund;
    private TextView tax;
    private TextView otherAdd;
    private TextView otherSub;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_details);
        initView();
    }
    private void initView()
    {
        baseSalary = (TextView)findViewById(R.id.base_salary);
        performSalary = (TextView)findViewById(R.id.perform_salary);
        commiSalary = (TextView)findViewById(R.id.commission_salary);
        penalty = (TextView)findViewById(R.id.penalty);
        bonus = (TextView)findViewById(R.id.bonus);
        salary = (TextView)findViewById(R.id.salary);
        realPay = (TextView)findViewById(R.id.real_salary);
        socialInsurance = (TextView)findViewById(R.id.social_insurance);
        healthInsurance = (TextView)findViewById(R.id.health_insurance);
        accumulationFund = (TextView)findViewById(R.id.accumulation_fund);
        tax = (TextView)findViewById(R.id.tax);
        otherAdd = (TextView)findViewById(R.id.other_add);
        otherSub = (TextView)findViewById(R.id.other_sub);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Map<String, String> map = (HashMap)bundle.getSerializable("data");
        baseSalary.setText(map.get("baseSalary"));
        performSalary.setText(map.get("performSalary"));
        commiSalary.setText(map.get("commiSarary"));
        penalty.setText(map.get("penalty"));
        bonus.setText(map.get("bonus"));
        salary.setText(map.get("sarary"));
        realPay.setText(map.get("realPay"));
        socialInsurance.setText(map.get("socialInsurance"));
        healthInsurance.setText(map.get("healthInsurance"));
        accumulationFund.setText(map.get("accumulationFund"));
        tax.setText(map.get("tax"));
        otherAdd.setText(map.get("otherAdd"));
        otherSub.setText(map.get("otherSub"));
    }
}
