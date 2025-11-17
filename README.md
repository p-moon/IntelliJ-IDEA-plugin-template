## 一、要解决什么问题

在日常的代码编写过程中，我们常常会遇到各种专有名词，难以为其选择恰当的变量名。很多时候，命名不够准确，或使用了不相关的词汇进行指代，即便如此，仍需耗费大量时间。

过去，我通常的做法是先临时起一个名称，编写好注释，然后全选代码复制给 Joycode，以帮助我替换方法名、函数名以及变量名。

然而，这种方式极为耗时，需要经历复制、粘贴、等待 AI 生成结果，再将结果复制回代码编辑器。因此，本次我们决定为 IDEA 开发一个自动补全插件，我将其命名为 Renamify。


## 二、功能描述

如下图所示，当输入“欲穷千里目”、“更上一层楼”等中文短语时，系统将生成简洁且规范的英文变量名建议。
![fcc14fa5-f240-41ce-b667-afd65f8b7e46.gif](https://s3.cn-north-1.jdcloud-oss.com/shendengbucket1/2025-11-17-10-44172XcjOSIP2MUMyA.gif)

![Kapture 2025-11-17 at 11.25.03.gif](https://s3.cn-north-1.jdcloud-oss.com/shendengbucket1/2025-11-17-11-25XEPHRCaghv9XStF.gif)

![image.png](https://s3.cn-north-1.jdcloud-oss.com/shendengbucket1/2025-11-17-10-54E0lFcusZvG94EBN.png)

## 三、安装说明

你可以通过如下地址下载这个插件的安装包：

- [renamify 插件下载](https://joyspace.jd.com/pages/XSnduyt9whBh9My5jDrN)

然后通过如下方式来安装使用：

![image.png](https://s3.cn-north-1.jdcloud-oss.com/shendengbucket1/2025-11-17-11-00ajTy32vSlBO17iFSb.png)

## 四、使用说明

插件安装后，我们可以在 IDEA 的 setting 菜单中看到多了一个配置项。

![image.png](https://s3.cn-north-1.jdcloud-oss.com/shendengbucket1/2025-11-17-11-07xAuwlT78BZNi3bd.png)

你可以在这里自定义你要使用的模型参数，包括提供给 LLM 的提示词。注意，提示词里面的 `%s` 不要去掉，这个是输入内容的占位符。

## 五、推荐的模型以及对 LLM 的一些要求和注意事项

### 5.1、推荐的模型

由于我是在本地做测试的，也没有额外的资源去购买 LLM 的 token，所以是在本地使用 ollma 来运行模型来使用的，如果你的办公本是最新的 MacBook Pro M 系列的芯片，那么你可以使用下面这两个模型：

|推荐模型| 说明 | 备注 |
| -- | -- | -- |
| gpt-oss:20b | 这个模型是 OpenAI 之前开源的一款 20B 参数的模型| 可以用，效果较好，就是算力、内存占用大，如果你是富哥，可以远端部署一个使用，本地部署也能凑活用，就是电脑容易卡顿 |
| qwen3:1.7b | 这个是阿里开源的一款小参数模型 | 直接在本地运行权重即可运行，内存和算力占用较小，体验极好，不存在任何信息安全问题(本地运行的哦) |

另外，所有的模型都不要开启思考模式，因为是实时补全，我们需要较快的响应，否则会导致 IDE 卡顿，影响使用体验， 当然，如果你不介意的话可以开启，目前插件内部强行通过 `reasoning_effort = none` 参数来禁用模型 的思考能力，所以选用的模型也必须支持这个参数。

**在自己的电脑本地启用 qwen3:1.7b**

```bash
brew install ollama && ollama run qwen3:1.7b
```


### 5.2、注意事项


如果你使用了自己购买的外部模型，建议咨询一下安全合规部门，看是否存在信息泄露的风险.


## 六、关于未来的展望

在上面我们已经通过AI 来自动生成了注释。是否也可以用来做更多的事情呢，比如按照上下文、关键字检索相关的历史功能点说明、技术文档、需求文档呢






