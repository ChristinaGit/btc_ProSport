package com.btc.prosport.di.subscreen;

import com.btc.prosport.di.subscreen.module.ProSportPresenterSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.screen.fragment.feedback.ProSportFeedbackFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo
    .ProSportAdditionalInfoFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.ProSportLogInFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn.ProSportReLogInFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp.ProSportSignUpFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {
    ProSportSystemSubscreenModule.class,
    ProSportPresenterSubscreenModule.class,
    ProSportRxSubscreenModule.class})
@SubscreenScope
public interface ProSportSubscreenComponent {
    void inject(ProSportAdditionalInfoFragment proSportAdditionalInfoFragment);

    void inject(ProSportLogInFragment proSportLogInFragment);

    void inject(ProSportSignUpFragment proSportSignUpFragment);

    void inject(ProSportReLogInFragment proSportReLogInFragment);

    void inject(ProSportFeedbackFragment proSportFeedbackFragment);
}
