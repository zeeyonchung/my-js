# 클래스를 사용하지 않는 방식의 상속

## 프로토타입을 활용한 상속

```
// 상속할 객체
var parent = {
    name: "Papa"
};

// 새로운 객체
var child = object(parent);

child.name; //Papa
```

parent라는 객체가 있고, parent와 동일한 프로퍼티와 메서드를 가지는 또 다른 객체 child를 생성하려고 한다.
```
function object(o) {
    function F() {}
    F.prototype = o;
    return new F();
}
```
빈 임시 생성자 함수 F()를 정의하고, F()의 프로토타입에 parent 객체를 지정한 후 임시 생성자의 새로운 인스턴스를 반환한다.

child 객체는 자기 자신의 프로퍼티를 가지지 않는 빈 객체이지만 \__proto\__ 링크로 parent의 모든 기능을 가지고 있다.

부모가 객체 리터럴로 생성되어야만 하는 것은 아니다. 생성자 함수를 통해 부모를 생성할 수도 있다.
```
// 부모 생성자
function Person() {
    // 부모 생성자 자신의 프로퍼티
    this.name = "John";
}

// 프로토타입에 추가된 프로퍼티
Person.prototype.getName = function () {
    return this.name;
};

// Person 인스턴스를 생성한다
var papa = new Person();

// 이 인스턴스를 상속한다
var kid = object(papa);

kid.name; //John
kid.getName(); //John

function object(o) {
    function F() {}
    F.prototype = o;
    return new F();
}
```
이 경우 부모 객체 자신의 프로퍼티와 생성자 함수의 프로토타입에 포함된 프로퍼티가 모두 상속된다는 점을 유의해야 한다.

생성자 함수의 프로토타입 객체만 상속받을 수 있도록 해보자.
```
// 부모 생성자
function Person() {
    // 부모 생성자 자신의 프로퍼티
    this.name = "John";
}

// 프로토타입에 추가된 프로퍼티
Person.prototype.getName = function () {
    return this.name;
};

// 이 인스턴스를 상속한다
var kid = object(Person.prototype);

kid.name; //undefined
typeof kid.getName; //function

function object(o) {
    function F() {}
    F.prototype = o;
    return new F();
}
```

#### Object.create()
ES5에서 프로토타입을 활용한 상속 패턴이 공식 요소가 되어 Object.create()이 이 패턴을 구현한다.  
```
var child = Object.create(parent);
```
```
var child = Object.create(parent, {
    age: {value: 2}
});

child.hasOwnProperty("age"); //true
```
두 번째 매개변수로 객체를 받아, 반환되는 child 객체 자신의 프로퍼티로 추가한다.  

## 프로퍼티 복사를 통한 상속 패턴
이 패턴은 객체가 다른 객체의기능을 단순히 복사를 통해 가져온다.
```
function extend(parent, child) {
    var i;
    child = child || {};
    for (i in parent) {
        if (parent.hasOwnProperty(i)) {
            child[i] = parent[i];
        }
    }
}

var dad = {name: "John"};
var kid = extend(dad);
kid.name; //John
```
부모의 멤버들에 대해 루프를 돌면서 자식에 복사한다. 하지만 얕은 복사로, 자식 쪽에서 객체타입인 프로퍼티 값을 수정하면 부모의 프로퍼티도 수정된다.  
깊은 복사를 수행하려면 다음과 같다.
```
function extendDeep(parent, child) {
    var i,
        toStr = Object.prototype.toString,
        astr = "[object Array]";

    child = child || {};

    for (i in parent) {
        if (parent.hasOwnProperty(i)) {
            if (typeof parent[i] === "object") {
                child[i[ = (toStr.call(parent[i]) === astr) ? [] : {};
                extendDeep(parent[i], child[i]);
            } else {
                child[i] = parent[i];
            }
        }
    }
    return child;
}
```