
const quill = new Quill('#editor', {
    theme: 'snow'
});
const fileObject = {};

function addAttribute(event) {
  const attributes = event.target.closest(".Attributes-container").querySelector(".attributes");
  const attributeContainers = [...document.getElementsByClassName("Attribute-container")];
  const newAttribute = attributeContainers.at(-1).cloneNode(true);

  const inputs = newAttribute.querySelectorAll('input');
  inputs.forEach(input => {
    input.value = ''; 
    const currentIndex = Date.now();
    
    if (input.name.includes('[name]')) {
      input.name = input.name.replace(/\[\d+\]\[name\]/, `[${currentIndex}][name]`);
    } else if (input.name.includes('[value]')) {
      input.name = input.name.replace(/\[\d+\]\[value\]/, `[${currentIndex}][value]`);
    }
  });

  
  attributes.append(newAttribute);
}

function removeAttributeContainer(event){
  if(event.target.closest(".attributes").querySelectorAll(".Attribute-container").length == 1) return;
  
  event.target?.closest(".Attribute-container")?.remove();
}

function addVariant(){
  const addVariantTable = document.querySelector(".add-variant-table");
  const variantInfo = document.querySelector(".variant-info");
  const newVariantInfo = variantInfo.cloneNode(true);
  const inputs = newVariantInfo.querySelectorAll("input");
  const newVariantIndex = Date.now();
  inputs.forEach(input  => {
    input.value = "";
    input.name = input.name.replace(/variants\[\d+\]/, `variants[${newVariantIndex}]`);
  });
  addVariantTable.append(newVariantInfo);
}

function removeVariant(event){
  if(document.querySelectorAll(".variant-info").length == 1) return;
  const variantInfo = event.target.closest(".variant-info");
  if(!variantInfo) return;
  variantInfo.remove();
}

const productForm = document.querySelector(".product-form");


function nextFormStep(event){
  const currentStep = document.querySelector(".cur-step");
  if(currentStep.dataset.index == 1 && isBasicInfoValid()){
    currentStep.classList.remove("cur-step");
    currentStep.classList.add("complete-step");
    document.querySelector(".step-indicator-circle-container:nth-child(2) .indicator-circle").classList.add("cur-step");
    document.querySelector(".basic-info").classList.add("hide");
    document.querySelector(".product-variant").classList.remove("hide");
  }
  if(currentStep.dataset.index == 2 && productForm.reportValidity()){
    currentStep.classList.remove("cur-step");
    currentStep.classList.add("complete-step");
    document.querySelector(".step-indicator-circle-container:nth-child(3) .indicator-circle").classList.add("cur-step");
    document.querySelector(".product-variant").classList.add("hide");
    document.querySelector(".product-images").classList.remove("hide");
    createTabs();
    event.currentTarget.querySelector(".text").innerText = "submit";
  }
  else if(currentStep.dataset.index == 3){

    if(fileObject["Default(optional)"] && fileObject["Default(optional)"].length != 0){
        submitForm();
        return;
    }
    for(const file in fileObject) {
      if(fileObject[file].length === 0 && file != "Default(optional)") {
         showAlert("Either upload one default image or upload image in all variant","error")
        return;
      }

    }
    submitForm();
  }
}
function prevFormStep(){
  const currentStep = document.querySelector(".cur-step");
  if(currentStep.dataset.index == 2){
    currentStep.classList.remove("cur-step");
   
    const prevStep = document.querySelector(".step-indicator-circle-container:nth-child(1) .indicator-circle");
    prevStep.classList.add("cur-step");
    prevStep.classList.remove("complete-step");
    document.querySelector(".product-variant").classList.add("hide");
    document.querySelector(".basic-info").classList.remove("hide");
  }
  if(currentStep.dataset.index == 3){
    currentStep.classList.remove("cur-step");
    const prevStep = document.querySelector(".step-indicator-circle-container:nth-child(2) .indicator-circle");
    prevStep.classList.add("cur-step");
    prevStep.classList.remove("complete-step");
    document.querySelector(".product-images").classList.add("hide");
    document.querySelector(".product-variant").classList.remove("hide");
    document.getElementById("nextStepBtn").querySelector(".text").innerText= "Next";
  }
}

function isBasicInfoValid() {
  const productName = productForm.productName;
  const productDescription = quill.root.textContent.trim();
  const category  = productForm.category;
  const brand = productForm.brand;
  const brandName = productForm.brandName;
  brand.reportValidity();
  if(productForm.category instanceof NodeList)
    category[0].reportValidity();
  else
    category.reportValidity();
  productName.reportValidity();
  brandName.reportValidity();
  if(productDescription.length === 0) {
    document.querySelector("#description-error").classList.remove("hide");
    setTimeout(() => {
      document.querySelector("#description-error").classList.add("hide");
    }, 2000);
    quill.focus();
    return false;
  }
  return productName.checkValidity() &&
         (productForm.category instanceof NodeList && category[0].checkValidity() || category.checkValidity()) &&
         brand.checkValidity() &&
         (brand.value !== "other" || brandName.checkValidity());
}

const qlEditor = document.querySelector(".ql-editor");
qlEditor.addEventListener("focus", function() {
  this.classList.add("focus");
});
qlEditor.addEventListener("blur", function() {
  this.classList.remove("focus");
});


const uploadBox = document.getElementById('uploadBox');
const fileInput = document.getElementById('fileInput');
const uploadImages = document.getElementById('uploadImages');

uploadBox.addEventListener('dragover', (e) => {
  e.preventDefault();
  uploadBox.classList.add('dragover');
});

uploadBox.addEventListener('dragleave', () => {
  uploadBox.classList.remove('dragover');
});

uploadBox.addEventListener('drop', (e) => {
  e.preventDefault();
  uploadBox.classList.remove('dragover');
  const files = e.dataTransfer.files;
  handleFiles(files);
});

uploadImages.addEventListener('click', () => fileInput.click());

fileInput.addEventListener('change', () => handleFiles(fileInput.files));


function storeFiles(variantName, files) {
    let arr = []
    Array.from(files).forEach((image)=>{
        if(image.size > 1024*1024){
            showAlert("image size can't more than 1mb","error");
        }
        else{
            arr.push(image);
        }

    })
    fileObject[variantName] = arr;

}

function displayFiles(variantName) {
  const imagePreviewContainer = document.querySelector('.tab-content:not(.hide) .image-preview-container');
  imagePreviewContainer.innerHTML = '';

  const files = fileObject[variantName] || [];


  document.querySelector(".tab.active .complete").src = files.length > 0
    ? "/images/icons/Check-circle.svg"
    : "/images/icons/Alert-triangle.svg";

  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = function (e) {
        const img = document.createElement('img');
        img.src = e.target.result;
        img.alt = 'Image Preview';
        img.draggable = false;
        img.dataset.name = file.name;

        const previewDiv = document.createElement('div');
        previewDiv.className = 'image-preview';
        previewDiv.appendChild(img);

        const removeBtn = document.createElement('button');
        removeBtn.className = 'remove-image-btn';
        removeBtn.ariaLabel = 'Remove Image';
        removeBtn.innerHTML = '<img src="/images/icons/Trash.svg" title="delete image" alt="" draggable="false" class="invert1">';

        removeBtn.addEventListener('click', () => {
          previewDiv.remove();
          fileObject[variantName] = fileObject[variantName]?.filter(f => f.name !== file.name);
          displayFiles(variantName);
        });

        previewDiv.appendChild(removeBtn);
        imagePreviewContainer.appendChild(previewDiv);
      };
      reader.readAsDataURL(file);
    }
  });
}


function handleFiles(files) {
  fileInput.files = null;
  const variantName = document.querySelector(".variant-img-name").textContent;
  storeFiles(variantName, files);
  displayFiles(variantName);
}


function createTabs(){
  const variantName = [...document.querySelectorAll(".sku")];
  [...document.querySelectorAll(".tab")].slice(1).forEach(ele=>ele.remove());
  document.querySelectorAll(".tab-content:not(#tab0)").forEach(ele=>ele.remove());
  for (let key in fileObject) {
    delete fileObject[key];
  }


  const tabs = document.querySelector(".tabs");
  const tabsContentContainer = document.querySelector(".tabs-content-container");
  console.log(tabs)
  console.log(tabsContentContainer)
  variantName.forEach((name, index) => {
    if(name.value.trim() === "") return;
    const newTab = document.querySelector(".tab").cloneNode(true);
    newTab.classList.remove("active");
    newTab.querySelector(".tab-name").textContent = name.value || `Variant ${index + 1}`;
    newTab.addEventListener('click', () => changeTab(newTab));
    newTab.dataset.tab = `tab${index + 1}`;
    newTab.dataset.variantName = name.value || `Variant ${index + 1}`;
    tabs.appendChild(newTab);
    if(fileObject[name.value] === undefined)
      fileObject[name.value] = [];
    const tabContent = document.querySelector(".tab-content").cloneNode(true);
    tabContent.classList.add("hide");
    tabContent.id = `tab${index + 1}`;
    tabContent.querySelector(".image-preview-container").remove();
    const div = document.createElement("div");
    div.className = "image-preview-container flex";
    tabContent.appendChild(div);
    tabsContentContainer.appendChild(tabContent);

  });
}

(function(){
    const tabButtons = document.querySelectorAll('.tab');

    Array.from(tabButtons).forEach(button => {
      button.addEventListener('click', () => {
        changeTab(button)
      });
    });
})()
function changeTab(button){
  const tabContent = document.querySelector('.tab-content:not(.hide)');
  const activeTab = document.querySelector('.tab.active');
  if (activeTab) {
    activeTab.classList.remove('active');
    tabContent.classList.add('hide'); 
  }


  document.querySelector(".variant-img-name").textContent = button.dataset.variantName || "Product Variant";
  button.classList.add('active');
  document.querySelector(`.tab-content#${button.dataset.tab}`).classList.remove('hide');
}
function selectBrand(){
  if(document.getElementById("brand").value === "other") {
    document.getElementById("brandName").classList.remove("hide");
    document.getElementById("brandName").required = true;
  }else{
    document.getElementById("brandName").classList.add("hide");
    document.getElementById("brandName").required = false;
  }
}

function normalizeProductData(data) {
  if (data.variants) {

    data.variants = Object.values(data.variants);
  }
  return data;
}

function setDeep(obj, path, value) {
  const keys = path.replace(/\]/g, "").split("[");
  let current = obj;
  keys.forEach((key, i) => {
    if (i === keys.length - 1) {
      current[key] = value;
    } else {
      const nextKey = keys[i + 1];
      const isArray = /^\d+$/.test(nextKey);
      if (!current[key]) current[key] = isArray ? [] : {};
      current = current[key];
    }
  });
}

function getFormData() {
  const form = document.querySelector("#productForm");
  const formData = new FormData(form);
  const data = {};
  for (const [name, value] of formData.entries()) {
    setDeep(data, name, value);
  }
  return data;
}
let a;

async function submitForm(){
  const productObject = normalizeProductData(getFormData());
  const allCategories = productForm.category;
  let curCategory ;
  for(let i = allCategories.length - 1; i >= 0; i--) {
    if(allCategories[i].value && allCategories[i].value != ""){
        curCategory = allCategories[i].value;
        break;
    }
  }
  productObject["category"] = curCategory;

  if(productObject["brandName"] != null){
    delete productObject["brandName"];
  }
  if(productObject["images"] != null){
    delete productObject["images"];
  }
  if(document.querySelector("#brand").value === "other") {
    productObject.brand = document.querySelector("#brandName").value;
  }


  productObject.description = quill.root.innerHTML;
  const formData = new FormData();


  productObject.variants.forEach(variant => {
    const files = fileObject[variant.sku] || [];
    variant.images = files.map(f => f.name);
  });
  productObject["defaultImageCount"] = fileObject["Default(optional)"]?.length || 0;
  formData.append("product", JSON.stringify(productObject));

  Object.entries(fileObject).forEach(([variantName, files]) => {
    files.forEach((file, index) => {
        if(variantName == "Default(optional)"){
             formData.append(`Default[${index}]`, file);
        }
        else{
             formData.append(`variantImages[${variantName}][${index}]`, file);
        }

    });
  });



  const nextStepBtn = document.querySelector("#nextStepBtn");
  nextStepBtn.disabled = true;
  try {
    const response = await fetch("/api/products", {
      method: "POST",
      headers:{
        "X-CSRF-TOKEN":  document.querySelector('meta[name="_csrf"]').content
      },
      body: formData
    });

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    nextStepBtn.disabled = false;
    window.location.href = "/admin/products?product-added=true";

  } catch (err) {
    nextStepBtn.disabled = false;
    showAlert("Unable to add product", "error")
    console.error("Error saving product", err);
  }
}

async function searchSku(query, inputEl) {
  if (!query) return;

  try {
    const response = await fetch(`/api/allsku/${encodeURIComponent(query)}`);
    if (!response.ok) throw new Error("Error fetching SKU data");

    const exists = await response.json();
    console.log("SKU exists:", exists);

    const skuErrorMessage = inputEl.nextElementSibling;

    if (exists) {
      inputEl.setCustomValidity("SKU already exists");
      skuErrorMessage.classList.remove("hide");
    } else {
      inputEl.setCustomValidity("");
      skuErrorMessage.classList.add("hide");
    }


//    inputEl.reportValidity();

  } catch (error) {
    console.error(error);
  }
}


function debounce(func, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => func.apply(this, args), delay);
  };
}


const debouncedSearch = debounce(searchSku, 500);


function checkSku(event) {
  const inputEl = event.target;


  const isUniqueInForm = validateUniqueSkus(inputEl);


  if (isUniqueInForm) {
    const sku = inputEl.value.trim();
    debouncedSearch(sku, inputEl);
  }
}

function validateUniqueSkus(changedInput = null) {
  const skuInputs = document.querySelectorAll("input.sku");
  const values = [];
  let hasDuplicate = false;

  skuInputs.forEach(input => {
    const value = input.value.trim();

    if (value && values.includes(value)) {
      input.setCustomValidity("Duplicate SKU in this form");

      hasDuplicate = true;
    } else {
       input.setCustomValidity("");
      values.push(value);
    }
  });


  if (changedInput) {
    return changedInput.checkValidity();
  }

  return !hasDuplicate;
}

const addCategoryModal = document.querySelector(".category-add-modal");
const addCategoryForm = document.querySelector(".category-add-form");
function closeAddCategory(){
    addCategoryModal.close();
}

document.querySelector(".open-add-category-btn").addEventListener("click", showAddCategory)
function showAddCategory(){
    addCategoryModal.showModal();
}

addCategoryForm.addEventListener("submit", function(event){
    event.preventDefault();
    const formSubmitBtn = document.querySelector(".submit-category-add-btn");
    const data = new FormData(event.target);

    formSubmitBtn.disabled = true;
    fetch("/api/categories",{
        method:"POST",
        headers:{
            "X-CSRF-TOKEN":  document.querySelector('meta[name="_csrf"]').content
        },
        body: data
    }).then(result => {
        if(result.ok)
        {
            showAlert("Category Add Successfully");
            return result.json();
        }
    }).then(result => {
           loadCategories();
        formSubmitBtn.disabled = false;

        closeAddCategory();
    }).catch(err => {
        showAlert("unknown Error", "error");
        console.error(err)
        formSubmitBtn.disabled = false;
    })
})

let categories;
const CategorySelectBox = document.querySelector("#Category");

function loadCategories(){
    fetch("/api/categories/parent")
        .then(result => result.ok ? result.json() : Promise.reject("bad response"))
        .then(result => {
            categories = result;
            generatedOptions(CategorySelectBox);
            generatedOptions(document.querySelector("#parent_category"));

        })
        .catch(err => {
            showAlert("unknown Error", "error");
            console.error(err)
        });
}
function generatedOptions(selectContainer){

    if(!selectContainer) return;
    selectContainer.innerHTML = "<option value=''>select</option>";
    categories.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.name;
        selectContainer.append(option);
    })
}
function handleSelectChange(event) {
    const selectedId = parseInt(event.target.value);
    const curCategory = findCategoryById(selectedId, categories);

    if (!curCategory) return;


    const currentBox = event.target.closest(".select-child");
    while (currentBox && currentBox.nextElementSibling) {
        currentBox.nextElementSibling.remove();
    }
    if (curCategory.children && curCategory.children.length > 0) {
        createSelectBox(curCategory);
    }
    else if (!document.querySelector(`.select-child option[data-id="${curCategory.id}"]`)) {
        document.querySelector(".dynamically-generated-selects").innerHTML = "";
    }

}
function handleSelectChangeMain(event) {
    const selectedId = parseInt(event.target.value);
    const curCategory = findCategoryById(selectedId, categories);

    if (!curCategory) return;


    document.querySelector(".dynamically-generated-selects").innerHTML = "";
    if (curCategory.children && curCategory.children.length > 0) {
        createSelectBox(curCategory);
    }
}

function createSelectBox(parentCategory){
    let options = `<option value="">select</option>`;
    parentCategory.children.forEach(c => {
        options += `<option value="${c.id}" data-id="${c.id}">${c.name}</option>`;
    });

    const copyBox = document.createElement("div");
    copyBox.className = "flex-column select-child";
    copyBox.innerHTML = `
        <label for="cat-${parentCategory.id}" class="input-label">${parentCategory.name}</label>
        <select name="category" id="cat-${parentCategory.id}" >
            ${options}
        </select>
    `;

    const container = document.querySelector(".dynamically-generated-selects");
    container.appendChild(copyBox);


    copyBox.querySelector("select").addEventListener("change", handleSelectChange);
}

function findCategoryById(id, nodes) {
    for (let node of nodes) {
        if (node.id === id) return node;
        if (node.children?.length) {
            const found = findCategoryById(id, node.children);
            if (found) return found;
        }
    }
    return null;
}

CategorySelectBox.addEventListener("change", handleSelectChangeMain);

loadCategories();