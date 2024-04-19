# ProMal
_ProMal_, a tool used to analyze malicious applications, can automatically extract **Malicious Behaviors Trajectory** (i.e., _MBT_) from malware and describe them in a human-readable way to inform users of malicious behaviors and remind users of potential security threats in detail.

## Our Advantages
ProMal not only performs well in _MBT_ extraction but also has ability to generate comprehensive descriptions. Most importantly, it can generate more precise and informative descriptions than XMal or family classification. In conclusion, _ProMal_ has the following advantages:
- **Precise and Fine-grained**. The approach should precisely identify MBTs from malware, which has richer semantic information in terms of understanding malware behaviors.
- **Scalable**. The approach is designed to be scalable and capable of addressing various types of malware and variants, especially adapting to the rapid iteration of malware.
- **Human-readable**. The malware description should be easily readable, facilitating user acceptance and understanding of how malicious behaviors are implemented.
  
## Use Case
- input: the malware you want to analyze. Taking malware AceCard0 as an example.
- output: the descriptions that describe which malware operations are performed by malicious software and how malicious behaviors are completed step by step. As shown in the following figure.
  <img width="60%" alt="image" src="https://github.com/ProMal4Android/ProMal/assets/158020802/98160279-f42f-4dcd-a2a9-d0cda99eca36">

## Tool Demo
Additionally, we have developed an online website as shown below to help security analysts or users more easily analyze malware. After users uploading their malware, in addition to the basic information, the website can also provides users with three aspects of malware analysis:
- **Key Features**. Key feature related to the realization of malicious behavior, including sensitive APIs, API-related permissions and some vital intents and string constants.
- **Malicious Behavior Trajectory**. All _MBT_ used to implement malicious behaviors.
- **Malware Descriptions**. Since the description is generated by the LLM, we simultaneously generate three paragraphs of description and design a user feedback mechanism to optimize the generated effect.
<img width="100%" alt="image" src="https://github.com/ProMal4Android/ProMal4Android/assets/158020802/4d5ee10f-214d-4fb2-8153-7a19c0339fb6">

The website is being deployed and released. Therefore, we use a demo to show the general flow of our tool and record a demo video. Note that we have hidden all the identity-related information and datas in the video does not represent the final implementation effect. Since the video size is limited to 10M, it is a pity that we cannot provide a more detailed and richer display.


https://github.com/ProMal4Android/ProMal/assets/158020802/d0255695-d64a-4780-af5d-294eb576f83b
