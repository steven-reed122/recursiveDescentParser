function greet() {
  console.log('Hello, World!');
}

function greetWithName(name) {
  console.log(`Hello, ${name}!`);
}

function greetWithTitleAndName(title, name) {
  console.log(`Hello, ${title} ${name}!`);
}

greet();
greetWithName("Steven");
greetWithTitleAndName("Mr","Steven");