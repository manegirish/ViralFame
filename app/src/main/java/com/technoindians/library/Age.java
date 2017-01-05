package com.technoindians.library;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 05/07/2016
 * Last modified 27/04/2016
 *
 */
public class Age
{
    private int days;
    private int months;
    private int years;

    public Age(int days, int months, int years)
    {
        this.days = days;
        this.months = months;
        this.years = years;
    }

    public int getDays()
    {
        return this.days;
    }

    public int getMonths()
    {
        return this.months;
    }

    public int getYears()
    {
        return this.years;
    }

    @Override
    public String toString()
    {
        return years + " Years, " + months + " Months, " + days + " Days";
    }
}
