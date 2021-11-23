const app = Vue.createApp({
    data() {
        return {
            applyForLoan: {
                name: "",
                amount: 0,
                payments: 0,
                accountNumber: ""
            },
            maxAmount: 0,
            installmentPayment: 0,
            clientData: [],
            availableLoans: [],
            paymentOptions: [],
            unavailableSection: true,
            selectedAccountId: 0
        }
    },
    created() {
        this.getClientData();
    },
    methods: {
        getClientData() {
            axios.get('/api/loans')
                .then(response => {
                    this.availableLoans = response.data
                })
                .catch(e => {
                    console.log(e)
                })

            axios.get('/api/clients/current')
                .then(response => {
                    this.clientData = response.data
                })
                .catch(e => {
                    console.log(e)
                })
        },
        selectedLoan(e) {
            this.unavailableSection = false
            let selection = this.availableLoans.find(loan => loan.name == e)
            this.paymentOptions = this.availableLoans.find(loan => loan.name == e).payments
            console.log(selection.maxAmount);
            this.maxAmount = selection.maxAmount
        },
        isLoanSelected(e) {
            return e == this.applyForLoan.name
        },
        applyNow(e) {
            console.log(e);
            console.log(this.clientData.accounts.find(account => account.number == e).account_id);

            this.selectedAccountId = this.clientData.accounts.find(account => account.number == e).account_id
            axios.post('/api/loans', this.applyForLoan)
                .then(e => {
                    console.log(e.data);
                    this.successMessage(e.data)
                }).catch(e => {
                    this.errorMessage(e.response.data)
                })
        },
        errorMessage(message) {
            swal({
                title: "Something went wrong",
                text: message,
                icon: "info",
                buttons: ["Check Loan Process"],
                dangerMode: false,
            }).then((e) => {
                if (e) {
                    window.location.href = "./loan-application.html"
                }
            })
        },
        successMessage(message) {
            swal({
                title: message,
                text: `Would you like to go to your Account N°${this.applyForLoan.accountNumber} page?`,
                icon: "success",
                buttons: ["No, thank you", "Yes, please"],
                dangerMode: false,
            }).then((e) => {
                if (e) {
                    window.location.href = `./account.html?id=${this.selectedAccountId}`
                }
            })
        },
        selectAccount(e) {
            this.selectedAccountId = this.clientData.accounts.find(account => account.accountNumber == e).account_id
        },
        logOut() {
            axios.post('/api/logout')
                .then(response => {
                    window.location.href = "./index.html"
                })
                .catch(error => {
                    console.log(error);
                })
        }

    },
    computed: {
        readyToApply() {
            this.installmentPayment = (this.applyForLoan.amount * 1.2 / this.applyForLoan.payments).toFixed(2)
            return this.applyForLoan.amount > 0 && this.applyForLoan.payments != 0 && this.applyForLoan.accountNumber != ""
        }

    }

})
app.mount("#app")

