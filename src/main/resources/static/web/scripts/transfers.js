const app = Vue.createApp({
    data() {
        return {
            switch_origin: false,
            switch_destination: false,
            fromEnabled: true,
            toEnabled: false,
            endEnabled: false,
            newTransfer: {
                origin: "",
                destination: "",
                amount: 1,
                description: ""
            },
            clientData: [],
            isNewAccount: true,
            agenda: {},
            option: 0,
            keys: []
        }
    },
    created() {
        this.getClientData();
    },
    methods: {
        getClientData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.clientData = response.data
                    this.agenda = this.clientData.agenda
                    this.keys = Object.keys(this.agenda)
                    const values = Object.entries(this.agenda)
                    this.agenda = values
                })
                .catch(e => {
                    console.log(e)
                })
        },
        isOriginSelected(e) {
            console.log(e)
            return e == this.newTransfer.origin
        },
        isDestinationSelected(e) {
            return e == this.newTransfer.destination
        },
        switchDestination() {
            this.newTransfer.destination = "";
            this.option = 0;
        },
        fullDestinationAccount() {
            return this.newTransfer.destination.includes('VIN') ? this.newTransfer.destination : `VIN-${this.newTransfer.destination}`
        },
        confirmTransfer() {
            swal({
                title: "Are you sure?",
                text: "Transfers have inmediatly effect on your account balance...",
                icon: "info",
                buttons: true,
            }).then((willTransfer) => {
                if (willTransfer) {
                    this.createNewTransfer()
                    swal("We are processing your request", {
                        icon: "success",
                    });
                } else {
                    swal("Trasnfer cancelled!");
                }
            });
        },
        createNewTransfer() {
            axios.post('/api/transactions', `originAccount=${this.newTransfer.origin}&destinationAccount=${this.fullDestinationAccount()}&amount=${this.newTransfer.amount}&description=${this.newTransfer.description}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    if (!this.keys.includes(this.newTransfer.destination) && !this.clientData.accounts.some((e) => e.number == this.newTransfer.destination)) {
                        swal("Done!", {
                            title: "Your transfer has been made succesfully!",
                            text: "Would you like to add this account to your agenda?",
                            buttons: true,
                            icon: "success"
                        }).then((e) => {
                            if (e) {
                                this.addAccountToAgenda()
                            }
                        })
                    } else {
                        swal("Done!", {
                            title: "Your transfer has been made succesfully!",
                            buttons: ["Take me to my accounts"],
                            icon: "success"
                        }).then(e => {
                            window.location.href = "./accounts.html"
                        })
                    }
                })
                .catch(error => {
                    swal({
                        title: "Something went wrong",
                        text: error.response.data,
                        icon: "info",
                        buttons: ["Restart process", "Take me to my accounts"],
                        dangerMode: false,
                    }).then((e) => {
                        if (e) {
                            window.location.href = "./accounts.html"
                        }
                    })
                })

        },
        addAccountToAgenda() {
            axios.post('/api/transactions/agenda/add', `accountNumber=${this.fullDestinationAccount()}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    swal("Done!", {
                        title: "Account added to your agenda!",
                        buttons: "Great!",
                        icon: "success"
                    })
                        .then(e => [
                            window.location.href = "./accounts.html"
                        ])
                })
                .catch(error => {
                    swal({
                        title: "Something went wrong",
                        text: error.response.data,
                        icon: "info",
                        buttons: "Take me to my accounts",
                        dangerMode: true,
                    }).then((e) => {
                        if (e) {
                            window.location.href = "./accounts.html"
                        } else {

                        }
                    })
                })
        },
        logOut() {
            axios.post('/api/logout')
                .then(response => {
                    window.location.href = "./index.html"
                })
                .catch(error => {
                    console.log(error);
                })
        },
    },
    computed: {
        enableTo() {
            return this.fromEnabled = this.newTransfer.origin != "";
        },
        enableEnd() {
            return this.endEnabled = this.enableTo && this.newTransfer.destination != ""
        },
        thereIsAgenda() {
            return Object.keys(this.agenda).length != 0;
        },
        isReadyToTransfer() {
            return this.newTransfer.origin != "" && this.newTransfer.destination != "" && this.newTransfer.amount > 0 && this.newTransfer.description != ""
        }
    }
})
const debug = app.mount("#app")

