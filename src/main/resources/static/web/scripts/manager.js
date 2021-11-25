const app = Vue.createApp({
    data() {
        return {
            loanName: "",
            maxAmount: "",
            interest: "",
            payments: "",
            description: ""
        }
    },
    methods: {
        logOut() {
            axios.post('/api/logout')
                .then(response => {
                    window.location.href = "./index.html"
                })
                .catch(error => {
                    console.log(error);
                })
        },

        onlyNumbersInPayment() {
            return this.payments.every(element => {
                return !isNaN(element);
            });
        },
        addLoan() {
            this.payments = this.payments.split(',')
            if (!this.onlyNumbersInPayment()) {
                window.alert("Please, set only numbers in payments separated by a comma")
                return;
            }
            axios.post('/api/loans/createLoan', {
                "id": 0,
                "name": this.loanName,
                "maxAmount": this.maxAmount,
                "payments": this.payments,
                "interest": this.interest,
                "description": this.description

            }).then(response => {
                window.alert(response.data)
            }).catch(e => {
                window.alert(e.response.data)

                console.log(e);
            })
        }

    }
})
const debug = app.mount("#app")