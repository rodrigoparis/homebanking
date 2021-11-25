



const app = Vue.createApp({
    data() {
        return {
            isReady: false,
            clientData: [],
            accountData: [],
            json: "",
            transactions: [],
            currentAccountID: 0,
            creditClass: 'creditClass',
            debitClass: 'debitClass',
            savings: 0,
            search: "",
            EUR: 1,
            USD: 0,
            ARS: 0
        }
    },
    created() {
        this.getIds();
        this.loadData();
    },
    methods: {
        spinnerOut() {
            document.getElementsByClassName("spinnerContainer")[0].classList.add("d-none")
            this.$refs.nav.classList.remove("d-none")
            this.$refs.main.classList.remove("d-none")
            this.$refs.footer.classList.remove("d-none")
            this.$refs.header.classList.remove("d-none")
        },
        createGraphic() {
            // var root = am5.Root.new("chartdiv");
            // var chart = root.container.children.push(
            //     am5xy.XYChart.new(root, {
            //         panY: false,
            //         layout: root.verticalLayout
            //     })
            // );

            // // Define data
            // var data = this.transactions
            // data = data.slice(0, 5).map(element => {
            //     let data =
            //     {
            //         "date": element.date.slice(5, 10),
            //         "balance": element.amount
            //     }
            //     return data;
            // }).reverse();


            // // Craete Y-axis
            // var yAxis = chart.yAxes.push(
            //     am5xy.ValueAxis.new(root, {
            //         renderer: am5xy.AxisRendererY.new(root, {})
            //     })
            // );
            // // Create X-Axis
            // var xAxis = chart.xAxes.push(
            //     am5xy.CategoryAxis.new(root, {
            //         renderer: am5xy.AxisRendererX.new(root, {}),
            //         categoryField: "date"
            //     })
            // );
            // xAxis.data.setAll(data);

            // // Create series
            // var series1 = chart.series.push(
            //     am5xy.ColumnSeries.new(root, {
            //         name: "Series",
            //         xAxis: xAxis,
            //         yAxis: yAxis,
            //         valueYField: "balance",
            //         categoryXField: "date"
            //     })
            // );
            // series1.data.setAll(data);
            // series1.columns.template.setAll({
            //     fillOpacity: 0.5,
            //     strokeWidth: 2,
            //     cornerRadiusTL: 5,
            //     cornerRadiusTR: 5,
            //     width: 30
            // });

            // // Add legend
            // var legend = chart.children.push(am5.Legend.new(root, {}));
            // legend.data.setAll(chart.series.values);

            // // Add cursor
            // chart.set("cursor", am5xy.XYCursor.new(root, {}));

            am4core.useTheme(am4themes_animated);
            var chart = am4core.create("chartdiv", am4charts.XYChart);
            chart.colors.list = [
                am4core.color("#17c4bb"),
                am4core.color("#17aac4"),
                am4core.color("#0b7b8f"),
                am4core.color("#0b2a8f"),
                am4core.color("#18067c"),
                am4core.color("#0f044d"),
            ];
            chart.data = this.transactions
            chart.data = chart.data.slice(0, 5).map(element => {
                let data =
                {
                    "date": element.date.slice(5, 10),
                    "balance": element.amount
                }
                return data;
            }).reverse();
            chart.padding(20, 20, 20, 20);
            var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
            categoryAxis.renderer.grid.template.location = 0;
            categoryAxis.dataFields.category = "date";
            categoryAxis.renderer.minGridDistance = 60;
            categoryAxis.title.text = "Date"
            var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
            valueAxis.title.text = "Balance"

            var series = chart.series.push(new am4charts.ColumnSeries());
            series.dataFields.categoryX = "date";
            series.dataFields.valueY = "balance";
            series.tooltipText = "${valueY.value}"
            series.columns.template.strokeOpacity = 0;
            var series2 = chart.series.push(new am4charts.LineSeries());
            series2.name = "Units";
            series2.stroke = am4core.color("#b2b8b7");
            series2.strokeWidth = 3;
            series2.dataFields.valueY = "balance";
            series2.dataFields.categoryX = "date";
            series2.smoothing = "monotoneX";
            var title = chart.titles.create();
            title.text = "Last 5 Movements";
            title.align = "center"
            title.fontSize = 25;
            title.marginBottom = 20;
            title.marginTop = 0


            chart.cursor = new am4charts.XYCursor();

            // as by default columns of the same series are of the same color, we add adapter which takes colors from chart.colors color set
            series.columns.template.adapter.add("fill", function (fill, target) {
                return chart.colors.getIndex(target.dataItem.index);
            });


        },
        getIds() {
            const urlSearchParams = new URLSearchParams(window.location.search);
            this.currentAccountID = urlSearchParams.get("id")
        },
        loadData() {
            axios.get(`https://free.currconv.com/api/v7/convert?q=EUR_ARS,EUR_USD&compact=ultra&apiKey=3336f2ed8252b2b54d39`)
                .then(response => {
                    ;
                    this.ARS = response.data.EUR_ARS
                    this.USD = response.data.EUR_USD
                })
                .catch(e => {

                })
            axios.get(`/api/accounts/${this.currentAccountID}`)
                .then(response => {
                    this.json = response
                    this.accountData = response.data
                    this.transactions = response.data.transactions.sort((a, b) => new Date(b.date) - new Date(a.date))

                    this.createGraphic();
                    this.isReady = true;

                })
                .catch(e => {

                })
            axios.get(`/api/clients/current`)
                .then(response => {
                    this.clientData = response.data
                    this.clientData.accounts.forEach(element => {
                        this.savings += element.balance;
                        setTimeout(() => {
                            this.spinnerOut();
                        }, 1000);
                    });
                })
                .catch(e => {

                })
        },
        currentDate() {
            const current = new Date();
            const date = `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`;
            return date;
        },
        getID(account) {
            return `./account.html?id=${account.account_id}&client_id=${this.clientData.id}`

        },
        logout() {
            axios.post('/api/logout')
                .then(response => {
                })
                .catch(error => console.log(error))
                .finally(
                    window.location.href = "./index.html"
                )
        },
        getSummary() {
            axios.post('/api/account/summary', `accountNumber=${this.accountData.number}`, { responseType: 'blob' })
                .then(response => {
                    let dis = response.headers['content-disposition'];
                    let file = decodeURI(dis.substring(21));
                    let link = document.createElement('a')
                    link.href = window.URL.createObjectURL(response.data)
                    link.download = file
                    link.click()
                    link.remove()
                })
                .catch(error => console.log(error))
        },
        deleteWarn() {
            swal({
                title: "WARNING INFORMATION",
                text: "If you agree you will no longer see your account information. The final deletion of transactions and account is subject to review by our financial agents.",
                icon: "info",
                buttons: ["Decline", "Accept"],
                dangerMode: true,
            }).then((e) => {
                if (e) {
                    console.log("A BORRAR CUENTA")
                    this.deleteAccount()
                }
            })
        },
        deleteAccount() {
            console.log("yendo a back");
            axios.post('/api/account/delete', `accountNumber=${this.accountData.number}`)
                .then(response => {
                    console.log(response);
                    swal({
                        title: "DONE!",
                        text: `Your account number ${this.accountData.number} is no long available. Fully deletion upon agent report`,
                        icon: "info",
                        buttons: "Ok",
                        dangerMode: false,
                    }).then((e) => {
                        if (e) {
                            console.log("HOLA");
                            window.location.href = "./accounts.html"
                        }
                    })
                }).catch(error => console.log(error))
        }

    },
    computed: {
        filteredTransactions() {
            return this.transactions.filter(transaction => transaction.description.toLowerCase()
                .includes(this.search.toLowerCase())
                || transaction.amount.toString().includes(this.search.toLowerCase()) || transaction.date
                    .includes(this.search)
                || transaction.type.toLowerCase().includes(this.search.toLowerCase()))
        },
        hasGraphics() {
            return this.transactions.length == 0
        }
    }
})
app.mount("#app")




