class myCustomConformationDialog extends HTMLElement{
    constructor(){
        super();
        this.shadow = this.attachShadow({mode : "open"});
        this.shadow.innerHTML = `

            <style>
                *{
                    margin:0;
                    padding:0;
                    box-sizing:border-box;
                }
                .customPopover{
                    background-color: var(--secondary-bg);
                    border: 1px solid var(--border-color);
                    min-width: min(300px, 100%);
                    padding: .8em 1em;
                    color: var(--main-text);
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    border-radius: .4em;
                }
                .customPopover::backdrop{
                    backdrop-filter: blur(2px);
                }
                .customPopover-content{
                    gap: .4em;
                }
                .customPopover-content p{
                    color: var(--secondary-text);
                    max-width: 300px;
                }

                .customPopover-action-btns{
                    margin-top: 1em;
                    width: fit-content;
                    & button{
                        padding: .8em 1.4em;
                        border: 1px solid;
                        border-radius: 100px;
                        font-size: 1rem;
                        font-weight: bold;
                        cursor: pointer;
                        user-select: none;
                    }
                    & button:focus{
                        outline: 2px solid;
                        outline-offset: 2px;
                    }

                }
                .customPopover-ok-btn{
                    background-color: rgb(227, 53, 53);
                    color: white;
                    margin-left: .4em;
                    &:hover{
                        box-shadow: 0px 0px 5px rgb(227, 53, 53);
                    }

                }
                .customPopover-cancel-btn{
                    background-color: var(--primary-bg);
                    color: var(--main-text);
                    &:hover{
                        box-shadow:0px 0px 5px ;
                    }
                }
            </style>

            <div popover  class="customPopover">
                <div class="customPopover-content flex-column">
                    <h2 class="customPopover-title">Title title title</h2>
                    <p class="customPopover-message">message message message message message message message</p>
                    <div class="customPopover-action-btns">
                        <button class="customPopover-cancel-btn">Cancel</button>
                        <button class="customPopover-ok-btn">Delete</button>
                    </div>
                </div>
            </div>
        `;
    }
    connectedCallback() {
        this.dialog = this.shadow.querySelector(".customPopover");
        this.titleEl = this.shadow.querySelector(".customPopover-title");
        this.messageEl = this.shadow.querySelector(".customPopover-message");
        this.okBtn = this.shadow.querySelector(".customPopover-ok-btn");
        this.cancelBtn = this.shadow.querySelector(".customPopover-cancel-btn");
    }

    show(title, message, callback) {
        this.titleEl.textContent = title;
        this.messageEl.textContent = message;

        this.cancelBtn.addEventListener("click", ()=>{
            this.dialog.hidePopover()
        } , {once : true})
        this.okBtn.addEventListener("click", ()=>{
            callback?.();
            this.dialog.hidePopover();
        }, {once : true});

        this.dialog.showPopover();
    }
}
customElements.define("custom-conformation-dialog", myCustomConformationDialog);
export const customConformationDialog = document.querySelector("custom-conformation-dialog");
