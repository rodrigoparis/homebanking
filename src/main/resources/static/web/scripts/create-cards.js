const app = Vue.createApp({
    data() {
        return {
            isDebit: true,
            isCredit: true,
            isTitanium: true,
            isGold: true,
            isSilver: true,
            switch_type: false,
            switch_benefits: false,
            isDisabled: true,
            isCheckable: true,
            newCard: {
                cardType: "DEBIT",
                cardColor: "TITANIUM"
            },
            cardClass: "TITANIUM",
            cardHolder: "NAME",
            validDate: new Date()
        }
    },
    created() {
        this.loadData();

    },
    methods: {
        loadData() {
            let year = this.validDate.toISOString().slice(2, 4)
            let month = this.validDate.toISOString().slice(5, 7)
            this.validDate = `${month}/${year}`
        },
        createNewCard() {
            axios.post('/api/clients/current/cards', `cardColor=${this.newCard.cardColor}&cardType=${this.newCard.cardType}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(
                    swal("Done!", {
                        title: "We'll redirect you to your cards page",
                        buttons: "OK!",
                        icon: "success"
                    })
                        .then((e) => {
                            if (e) {
                                window.location.href = "./cards.html"
                            }
                        })
                )
                .catch(error => {
                    swal({
                        title: "Something went wrong",
                        text: error.response.data,
                        icon: "info",
                        buttons: ["Restart process", "Take me to my cards"],
                        dangerMode: false,
                    }).then((e) => {
                        if (e) {
                            window.location.href = "./cards.html"
                        } else {
                            this.isDebit = true
                            this.isCredit = true
                            this.isTitanium = true
                            this.isGold = true
                            this.isSilver = true
                            this.switch_type = false
                            this.switch_benefits = false
                            this.isDisabled = true
                            this.isCheckable = true
                        }
                    })
                })
        },
        thruDate(card) {

        },
        getID(account) {
            return `./account.html?id=${account.account_id}&client_id=${this.clientData.id}`
        },
        logOut() {
            console.log("LOGOUT")
            axios.post('/api/logout')
                .then(response => {
                    console.log('signed out!!!')
                })
                .catch(error => {
                    console.log(error);
                })
        },
        selectType(e) {
            this.switch_type = true;
            if (e.target.value == "DEBIT") {
                this.isCredit = false;
                this.isDebit = true;
            } else {
                this.isCredit = true;
                this.isDebit = false;
            }
            this.isDisabled = false;
         

        },
        switchTypeFx() {
            this.isCredit = true;
            this.isDebit = true;
            this.switch_type = false;
            this.isDisabled = true;
            this.isCheckable = true;
        },
        selectBenefits(e) {
            this.switch_benefits = true;
            if (e.target.value == "TITANIUM") {
                this.isTitanium = true;
                this.isGold = false;
                this.isSilver = false;
            } else if (e.target.value == "GOLD") {
                this.isGold = true;
                this.isTitanium = false;
                this.isSilver = false;
            } else {
                this.isSilver = true;
                this.isTitanium = false;
                this.isGold = false;
            }
            this.isCheckable = false;
        },
        switchBenefitsFx() {
            this.isTitanium = true;
            this.isGold = true;
            this.isSilver = true;
            this.switch_benefits = false;
            this.isCheckable = true;
        }
    }
})
const debug = app.mount("#app")

