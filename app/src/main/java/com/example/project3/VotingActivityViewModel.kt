package com.example.project3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VotingActivityViewModel(): ViewModel() {
    var checkBoxValueList= MutableLiveData<MutableList<Boolean>>( mutableListOf<Boolean>())

}