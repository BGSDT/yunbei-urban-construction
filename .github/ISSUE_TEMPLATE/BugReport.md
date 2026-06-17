name: Bug 反馈
about: 在使用 云北城建 的过程中遇到了 Bug。
title: ''
labels: bug
body:
  - type: markdown
    attributes:
      value: |
        👆请在上面攥写你的标题，尽可能的直观，以便开发者能够快速了解这个问题。
        
        > [!important]
        > 如果要提出多个 Bug，请为每一个 Bug 开一个单独的 issue。
        
  - type: checkboxes
    id: checklist
    attributes:
      label: 检查清单
      description: 在开始撰写这个 issue 前，请先检查：
      options:
        - label: 我已更新到最新版的云北城建，确认此 Bug 还未被修复。我也已在 [Issues](https://github.com/BGSDT/yunbei-urban-construction/issues) 中检索，确认此 Bug 未被其他人提交过。
          required: true
        - label: 我已知晓此板块适用于提交模组本身问题，并非提出功能特点。
          required: true
        - label: 我已知晓自己所用的硬件能够流畅运行此模组，且运行场景为桌面端。
          required: true
        - label: 我已确认是因云北城建导致游戏出现问题。
          required: true
        
  - type: markdown
    attributes: 
      value: |
        ### Bug 信息

        描述你所发现的 Bug。
        1. 游戏日志：请附上完整的游戏日志文件，如遇到崩溃以及闪退等问题，可以并附带 crash 信息。
        2. 模组版本：请明确说明您使用的游戏版本号。

        > [!note]
        > 上传附件时请使用 GitHub 的附件系统上传附件，将需要上传的附件粘贴或拖动到撰写区域即可上传。多个文件可打包为 zip 格式后上传。
  - type: textarea
    id: excepted
    attributes:
      label: 正确的行为
      description: 详细的描述你心中认为此问题中应该修复后的效果。
    validations:
      required: true
  - type: textarea
    id: reproduce-steps
    attributes:
      label: 重现步骤
      description: |
        详细描述问题是如何触发的、具体操作流程。
      placeholder: |
        1. 首先……
        2. 然后……
        3. ……
    validations:
      required: true
  - type: textarea
    id: screenshots
    attributes:
      label: 运行截图
      description: |
        截图请包含游戏 F3 调试界面（方便查看方块信息），崩溃问题可选择不上传截图。
  - type: textarea
    id: stacktrace
    attributes:
      label: 堆栈跟踪（可选）
      description: 如果在遇到这个 Bug 时发生了崩溃，部分启动器会给出可能的错误原因，请将产生的信息粘贴到此处，辅助开发者定位原因。
      render: shell
  - type: checkboxes
    id: revision
    attributes:
      label: 最后一步
      description: 回顾表单
      options:
        - label: 我认为上述的描述已经十分详细，能够让开发人员能复现该问题且修复此 Bug。如果我的 issue 没有按照上述的要求填写，可能会被无视。
          required: true
