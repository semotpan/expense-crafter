package com.semotpan.expensecrafter.spendingplan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultPlan {

    // TODO add externalization (resource_bundle)
    NECESSITIES("Necessities", 55, "Necessities spending: Rent, Food, Bills etc."),
    LONG_TERM_SAVING("Long Term Savings", 10,
            "Long Term Savings spending: Big Purchases, Vacations, Rainy Day Found, Unexpected Medical Expenses."),
    EDUCATION("Education", 10,
            "Education spending: Coaching, Mentoring, Books, Courses, etc."),
    PLAY("Play", 10,
            "Play spending: Spoiling yourself & your family, Leisure expenses, Fun, etc."),
    FINANCIAL("Financial", 10,
            "Financial spending: Stocks, Mutual Funds, Passive income Vehicles, Real Estate investing, Any other investments."),
    GIVE("Give", 5, "Give spending: Charitable, Donations.");

    private final String name;
    private final int percentage;
    private final String description;

    public static final String NAME = "Default spending plan";
    public static final String DESCRIPTION = "Default plan distribution: Necessities(55%), Long Term Savings(10%), Education(10%), Play(10%), Financial(10%), Give(5%).";

}
