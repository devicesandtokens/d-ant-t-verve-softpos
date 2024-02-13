[![Alcineo](http://alcineo.com/wp-content/uploads/Logo-Alcineo-small-200x55.jpg "Alcineo")](https://alcineo.com/)

# Bon Appetit demo application

## About / Synopsis

* This project is an example of how you can use the Alcineo Softpos

After launching the application, you'll find the **food selection** page.
Add some food item to your basket and click on **Charge** for start payment demonstration.


## Installation

* From Android studio: simply open the project in Android studio, wait for dependency downloads to finish and run the project on
  your Android device

* From the command line:
  `adb install -r *.apk`

### Content

Application is based on [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) with [MVVM design pattern](https://www.toptal.com/android/android-apps-mvvm-with-clean-architecture).

Only one [activity](app/src/main/java/com/alcineo/bonappetit/ui/MainActivity.java) is used, and contain a [FragmentContainerView](https://developer.android.com/reference/androidx/fragment/app/FragmentContainerView) responsible for the navigation.

Fragments are:
- [Shopping Fragment](app/src/main/java/com/alcineo/bonappetit/ui/shopping/ShoppingFragment.java) used to select food items and place them in basket.
- [Transaction fragment](app/src/main/java/com/alcineo/bonappetit/ui/transaction/TransactionFragment.java) used to perform the transaction
- [Receipt fragment](app/src/main/java/com/alcineo/bonappetit/ui/receipt/ReceiptFragment.java) used to display transaction ending items as ticket.
- [Settings fragment](app/src/main/java/com/alcineo/bonappetit/ui/settings/SettingsFragment.java) used to store to device preferences as transactionCurrency.


### Components used in this sample

* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
* [MVVM design pattern](https://www.toptal.com/android/android-apps-mvvm-with-clean-architecture)
* [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started)
* [Recyclerview](https://developer.android.com/guide/topics/ui/layout/recyclerview)
* [Binding adapter](https://developer.android.com/reference/android/databinding/BindingAdapter)
* [Workmanager](https://developer.android.com/topic/libraries/architecture/workmanager)



## Copyright Alcineo

Copyright (c) 2007-2020 ALCINEO All Rights Reserved. This software is the confidential and proprietary information of ALCINEO
("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in accordance with the
terms of the license agreement you entered into with ALCINEO. ALCINEO makes no representations or warranties about the suitability
of the software, either express or implied, including but not limited to the implied warranties of merchantability, fitness for a
particular purpose or non-infringement. ALCINEO shall not be liable for any damages suffered by licensee as the result of using,
modifying or distributing this software or its derivatives.
