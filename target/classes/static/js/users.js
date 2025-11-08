
function fetchData(){
    fetch("/api/users")
    .then(re => {

        if(!re.ok) throw new Error();
        return re.json();
    })
    .then(re => {
//        console.log(re)
        renderData(re);
    })
    .catch(err => {
        console.log(err);
    })
}

function renderData(data){
    let row = "";
    console.log(data);
    data.forEach((re) => {
        row += `
            <tr>
                <td>${re.email}</td>
                <td>${re.firstName}</td>
                <td>${re.lastName}</td>
                <td>${re.roleName}</td>
                <td>
                    <div class="flex">
                        <button class="action-btn" >
                            <img src="/images/icons/Edit.svg" alt="" class="invert-img">
                        </button>
                        <button class="action-btn" >
                            <img src="/images/icons/delete.svg" class="invert-img" alt="">
                        </button>
                    </div>
                </td>
            </tr>
        `
    });
    document.querySelector(".user-table tbody").innerHTML = row;
}
fetchData();