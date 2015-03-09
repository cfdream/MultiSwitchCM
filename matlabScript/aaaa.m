function aaaa() 

    data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\diffMemory_diffRatio_Rp_Select_vs_Accuracy_FalseNegative\diffMemory_vs_samplehold_replacement.txt');

    %size
    sizeData=size(data);
    rows=sizeData(1);

    %one tenth memory
    hundredthMem=data(1,2);
    
    accuracy(1,1,1,1)=1;
    accuracySD(1,1,1,1)=1;
    fn(1,1,1)=1;
    fnSD(1,1,1)=1;
    for i=1:rows
        isRp=data(i,1)+1;
        brTime=log2(data(i,8))+1;
        ithMem=ithMemory(hundredthMem, data(i,2));
        isSelect=data(i,11)+1;
        accuracy(isRp, isSelect, brTime, ithMem)=data(i,3);
        accuracySD(isRp, isSelect, brTime, ithMem)=data(i,4);
        fn(isRp, isSelect, brTime, ithMem)=data(i,5);
        fnSD(isRp, isSelect, brTime, ithMem)=data(i,6);
    end
        
    %accuracy
    %1. isRp{1-N:sh,2-Y:replace}, 2. isSelect{1-N,2-Y} 3. byte rate times{x}
    figure
    hold on;    
    %sh+ratio2
    errorbar(3:7, accuracy(1,1,2,3:end), accuracySD(1,1,2,3:end), '-rv', 'linewidth', 2);
    %sh+replace+ratio2
    errorbar(3:7, accuracy(2,1,2,3:end), accuracySD(2,1,2,3:end), '-g>', 'linewidth', 2);
    %sh+select+ratio2
    errorbar(3:7, accuracy(1,2,2,3:end), accuracySD(1,2,2,3:end), '-b^', 'linewidth', 2);
    %sh+replace+select+ratio2
    errorbar(3:7, accuracy(2,2,2,3:end), accuracySD(2,2,2,3:end), '-mo', 'linewidth', 2);
    %sh+ratio1
    errorbar(1:7, accuracy(1,1,1,:), accuracySD(1,1,1,:), '-.rv', 'linewidth', 2);

    set(gca, 'FontSize', 18,'XTick', 1:7, 'xticklabel', {2060, 10302, 20604, 51510, 103020, 154530, 206040});
    legend('SH-2','SH+rep-2','SH+sel-2','SH+rep+sel-2', 'SH-1');
    legend('Location', 'southeast');
    legend('boxoff');
    xlabel('#buckets in hashmap')
    ylabel('accuracy')
    xlim([2.95,7.05])
    ylim([0.71,0.96])
    hold off;
    
    %false negative
    %1. isRp{1-N:sh,2-Y:replace}, 2. isSelect{1-N,2-Y} 3. byte rate times{x}
    figure
    hold on;    
    %sh+ratio2
    errorbar(3:7, fn(1,1,2,3:end), fnSD(1,1,2,3:end), '-rv', 'linewidth', 2);
    %sh+replace+ratio2
    errorbar(3:7, fn(2,1,2,3:end), fnSD(2,1,2,3:end), '-g>', 'linewidth', 2);
    %sh+select+ratio2
    errorbar(3:7, fn(1,2,2,3:end), fnSD(1,2,2,3:end), '-b^', 'linewidth', 2);
    %sh+replace+select+ratio2
    errorbar(3:7, fn(2,2,2,3:end), fnSD(2,2,2,3:end), '-mo', 'linewidth', 2);
    %sh+ratio1
    errorbar(1:7, fn(1,1,1,:), fnSD(1,1,1,:), '-.rv', 'linewidth', 2);

    set(gca, 'FontSize', 18,'YTick', 0.01:0.02:0.15,'XTick', 1:7, 'xticklabel', {2060, 10302, 20604, 51510, 103020, 154530, 206040});
    legend('SH-2','SH+rep-2','SH+sel-2','SH+rep+sel-2', 'SH-1');
    legend('Location', 'northeast');
    legend('boxoff');
    xlabel('#buckets in hashmap')
    ylabel('accuracy')
    xlim([2.95,7.05])
    ylim([0.01,0.16])
    hold off;
    
end

function idx=ithMemory(hundredthMem, mem)
    if hundredthMem == mem
        idx=1;
        return;
    end
    times=int16(mem/hundredthMem);
    
    %idx=int16(times/2.5)+1;
    %{0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1 }
    if times==1
        idx=1;
    end
    if times==5
        idx=2;
    end
    if times==10
        idx=3;
    end
    if times==25
        idx=4;
    end
    if times==50
        idx=5;
    end
    if times==75
        idx=6;
    end
    if times==100
        idx=7;
    end
end
